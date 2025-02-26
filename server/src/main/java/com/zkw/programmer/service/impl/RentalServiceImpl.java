package com.zkw.programmer.service.impl;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zkw.programmer.bean.CodeMsg;
import com.zkw.programmer.dao.EquipmentMapper;
import com.zkw.programmer.dao.RentalMapper;
import com.zkw.programmer.dao.UserMapper;
import com.zkw.programmer.dao.my.MyEquipmentMapper;
import com.zkw.programmer.dao.my.MyRentalMapper;
import com.zkw.programmer.domain.Equipment;
import com.zkw.programmer.domain.Rental;
import com.zkw.programmer.domain.RentalExample;
import com.zkw.programmer.domain.User;
import com.zkw.programmer.dto.PageDTO;
import com.zkw.programmer.dto.RentalDTO;
import com.zkw.programmer.dto.ResponseDTO;
import com.zkw.programmer.dto.UserDTO;
import com.zkw.programmer.enums.EquipmentStateEnum;
import com.zkw.programmer.enums.RentalStateEnum;
import com.zkw.programmer.enums.RoleEnum;
import com.zkw.programmer.service.IRentalService;
import com.zkw.programmer.service.IUserService;
import com.zkw.programmer.utils.CommonUtil;
import com.zkw.programmer.utils.CopyUtil;
import com.zkw.programmer.utils.UuidUtil;
import com.zkw.programmer.utils.ValidateEntityUtil;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;


@Service
public class RentalServiceImpl implements IRentalService {

    @Resource
    private RentalMapper rentalMapper;

    @Resource
    private MyRentalMapper myRentalMapper;

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private IUserService userService;

    @Resource
    private UserMapper userMapper;

    @Resource
    private MyEquipmentMapper myEquipmentMapper;

    @Resource
    private EquipmentMapper equipmentMapper;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    //redis键名模板
    private final static String EQUIPMENT_REDIS_KEY_TEMPLATE = "equipment_%s";
    private final static String RENTAL_REDIS_KEY_TEMPLATE = "stream.rental";

    private static final Logger logger = LoggerFactory.getLogger(RentalServiceImpl.class);

    private static final ExecutorService RENTAL_UPDATE_EXECUTOR = Executors.newSingleThreadExecutor();

    @PostConstruct
    private void init() {
        RENTAL_UPDATE_EXECUTOR.submit(new RentalHandler());
    }


    private class RentalHandler implements Runnable {

        @Override
        public void run() {
            while (true) {
                try {
                    // 1.获取消息队列中的租借信息 XREAD GROUP g1 c1 COUNT 1 BLOCK 2000 STREAMS stream.orders >
                    // ReadOffset.lastConsumed() 获取下一个未消费的租借数据
                    List<MapRecord<String, Object, Object>> list = stringRedisTemplate.opsForStream().read(
                            Consumer.from("g1", "c1"),
                            StreamReadOptions.empty().count(1).block(Duration.ofSeconds(2)),
                            StreamOffset.create(RENTAL_REDIS_KEY_TEMPLATE, ReadOffset.lastConsumed())
                    );
                    // 2.判断租借信息是否为空
                    if (list == null || list.isEmpty()) {
                        // 如果为null，说明没有消息，继续下一次循环
                        continue;
                    }
                    // 解析数据 获取一条数据  因为上面count(1)指定获取一条
                    MapRecord<String, Object, Object> record = list.get(0);
                    Map<Object, Object> value = record.getValue();
                    String jsonRentalString = (String) value.get("rentalData");
                    Rental rental = JSON.parseObject(jsonRentalString, Rental.class);
                    // 3.更新租借数据
                    logger.info("接收到消息队列数据，准备更新...");
                    rentalMapper.updateByPrimaryKeySelective(rental);
                    logger.info("租借数据更新完成...");
                    // 4.确认消息 XACK
                    stringRedisTemplate.opsForStream().acknowledge(RENTAL_REDIS_KEY_TEMPLATE, "g1", record.getId());
                } catch (Exception e) {
//                    logger.error("处理租借数据异常", e);
                    handlePendingList();
                }
            }
        }

        // 确认异常的租借数据再次处理
        private void handlePendingList() {
            while (true) {
                try {
                    // 1.获取pending-list中的租借信息 XREAD GROUP g1 c1 COUNT 1 BLOCK 2000 STREAMS stream.orders 0
                    // ReadOffset.from("0") 从第一个消息开始
                    List<MapRecord<String, Object, Object>> list = stringRedisTemplate.opsForStream().read(
                            Consumer.from("g1", "c1"),
                            StreamReadOptions.empty().count(1),
                            StreamOffset.create(RENTAL_REDIS_KEY_TEMPLATE, ReadOffset.from("0"))
                    );
                    // 2.判断租借信息是否为空
                    if (list == null || list.isEmpty()) {
                        // 如果为null，说明没有异常消息，结束循环
                        break;
                    }
                    // 解析数据
                    MapRecord<String, Object, Object> record = list.get(0);
                    Map<Object, Object> value = record.getValue();
                    String jsonRentalString = (String) value.get("rentalData");
                    Rental rental = JSON.parseObject(jsonRentalString, Rental.class);
                    // 3.更新租借数据
                    logger.info("接收到消息队列数据，准备更新...");
                    rentalMapper.updateByPrimaryKeySelective(rental);
                    logger.info("租借数据更新完成...");
                    // 4.确认消息 XACK
                    stringRedisTemplate.opsForStream().acknowledge(RENTAL_REDIS_KEY_TEMPLATE, "g1", record.getId());
                } catch (Exception e) {
//                    logger.error("处理租借数据异常", e);
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException interruptedException) {
//                        interruptedException.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * 分页获取租借数据
     * @param pageDTO
     * @return
     */
    @Override
    public ResponseDTO<PageDTO<RentalDTO>> getRentalListByPage(PageDTO<RentalDTO> pageDTO, String token) {
        ResponseDTO<UserDTO> loginUserResponse = userService.getLoginUser(token);
        if(!CodeMsg.SUCCESS.getCode().equals(loginUserResponse.getCode())) {
            return ResponseDTO.errorByMsg(CodeMsg.USER_SESSION_EXPIRED);
        }
        UserDTO loginUserDTO = loginUserResponse.getData();
        RentalExample rentalExample = new RentalExample();
        // 判断是否进行关键字搜索
        RentalDTO searchRentalDTO = pageDTO.getSearchEntity();
        RentalExample.Criteria criteria = rentalExample.createCriteria();
        if(!CommonUtil.isEmpty(searchRentalDTO.getId())) {
            criteria.andIdLike("%" + searchRentalDTO.getId() + "%");
        }
        // 如果是普通用户，只能看到自己的信息
        if(RoleEnum.USER.getCode().equals(loginUserDTO.getRoleId())) {
            criteria.andUserIdEqualTo(loginUserDTO.getId());
        }
        // 不知道当前页多少，默认为第一页
        if(pageDTO.getPage() == null){
            pageDTO.setPage(1);
        }
        if(pageDTO.getSize() == null) {
            pageDTO.setSize(5);
        }
        rentalExample.setOrderByClause("create_time desc");
        PageHelper.startPage(pageDTO.getPage(), pageDTO.getSize());
        // 分页查出租借数据
        List<Rental> rentalList = rentalMapper.selectByExample(rentalExample);
        PageInfo<Rental> pageInfo = new PageInfo<>(rentalList);
        // 获取数据的总数
        pageDTO.setTotal(pageInfo.getTotal());
        // 多线程处理租借信息的状态
        try {
            rentalList = operateRentalListState(rentalList, pageDTO.getTotalPage(), pageDTO.getSize());
        } catch (Exception e) {
            logger.error(e.getMessage());
            return ResponseDTO.errorByMsg(CodeMsg.RENTAL_OPERATE_ERROR);
        }
        // 将domain类型数据  转成 DTO类型数据
        List<RentalDTO> rentalDTOList = CopyUtil.copyList(rentalList, RentalDTO.class);
        for(RentalDTO rentalDTO : rentalDTOList) {
            User user = userMapper.selectByPrimaryKey(rentalDTO.getUserId());
            rentalDTO.setUserDTO(CopyUtil.copy(user, UserDTO.class));
        }
        pageDTO.setList(rentalDTOList);
        return ResponseDTO.success(pageDTO);
    }

    /**
     * 保存租借数据(添加、修改)
     * @param rentalDTO
     * @return
     */
    @Override
    public ResponseDTO<Boolean> saveRental(RentalDTO rentalDTO) {
        // 进行统一表单验证
        CodeMsg validate = ValidateEntityUtil.validate(rentalDTO);
        if(!validate.getCode().equals(CodeMsg.SUCCESS.getCode())){
            return ResponseDTO.errorByMsg(validate);
        }
        Rental rental = CopyUtil.copy(rentalDTO, Rental.class);
        ResponseDTO<Boolean> responseDTO = ResponseDTO.successByMsg(true, "保存成功！");
        if(CommonUtil.isEmpty(rental.getId())){
            // id为空 说明是添加数据
            // 生成8位id
            rental.setId(UuidUtil.getShortUuid());
            rental.setCreateTime(new Date());
            rental.setState(RentalStateEnum.WAIT.getCode());
            String redissonKey = String.format(EQUIPMENT_REDIS_KEY_TEMPLATE, rental.getEquipmentId());
            RLock lock = redissonClient.getLock(redissonKey);
            //1.加锁  阻塞获取锁：获取不到一直循环尝试获取
            lock.lock();
            try {
                //  @Transactional 事务执行完后  再unlock释放锁
                //  为了避免锁在事务提交前释放，我们应该在事务外层使用锁。
                responseDTO = createRental(rental);
            }catch (Exception e){
                logger.error(e.getMessage());
            }finally {
                //解锁
                lock.unlock();
            }
        } else {
            // id不为空 说明是修改数据
            // 修改数据库中数据
            responseDTO = updateRental(rental);

        }
        return responseDTO;
    }

    /**
     * 更新租借信息
     * @param rental
     * @return
     */
    @Transactional
    public ResponseDTO<Boolean> updateRental(Rental rental) {
        if(RentalStateEnum.FAIL.getCode().equals(rental.getState()) || RentalStateEnum.CANCEL.getCode().equals(rental.getState())) {
            myEquipmentMapper.addRentalNum(rental.getNum(), rental.getEquipmentId());
        }
        if(rentalMapper.updateByPrimaryKeySelective(rental) == 0){
            throw new RuntimeException(CodeMsg.RENTAL_EDIT_ERROR.getMsg());
        }
        return ResponseDTO.successByMsg(true, "保存成功！");
    }


    /**
     * 多线程处理租借信息的状态
     * @param rentalList
     * @param pages
     * @param pageSize
     * @return
     */
    @Transactional
    @Override
    public List<Rental> operateRentalListState(List<Rental> rentalList, Integer pages, Integer pageSize) throws InterruptedException {
        // 多线程更改租借信息的状态
        ExecutorService executorService = Executors.newFixedThreadPool(pages);
        CountDownLatch countDownLatch = new CountDownLatch(pages);
        List<Rental> newRentalList = new ArrayList<>();
        for (int i = 0; i < pages; i++) {
            int finalI = i;
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    List<Rental> subRentalList = rentalList.stream().skip((finalI) * pageSize).limit(pageSize).collect(Collectors.toList());
                    for (Rental rental : subRentalList) {
                        if(RentalStateEnum.SUCCESS.getCode().equals(rental.getState())) {
                            // 如果已经审核通过，状态自动改为已完成或者租借中
                            if(new Date().getTime() >= rental.getEndTime().getTime()) {
                                rental.setState(RentalStateEnum.FINISH.getCode());
                                // 创建消息记录, 以及指定stream
                                insertRedisStream(rental);
                                // 体育器材数量恢复
                                myEquipmentMapper.addRentalNum(rental.getNum(), rental.getEquipmentId());
                            } else if(new Date().getTime() >= rental.getStartTime().getTime()) {
                                rental.setState(RentalStateEnum.RENTAL.getCode());
                                // 创建消息记录, 以及指定stream
                                insertRedisStream(rental);
                            }
                        } else if (RentalStateEnum.RENTAL.getCode().equals(rental.getState())) {
                            // 如果是在租借中，状态自动改为已完成
                            if(new Date().getTime() > rental.getEndTime().getTime()) {
                                rental.setState(RentalStateEnum.FINISH.getCode());
                                // 创建消息记录, 以及指定stream
                                insertRedisStream(rental);
                                // 体育器材数量恢复
                                myEquipmentMapper.addRentalNum(rental.getNum(), rental.getEquipmentId());
                            }
                        }
                        newRentalList.add(rental);
                    }

                    countDownLatch.countDown();
                }
            });
        }
        countDownLatch.await();

        executorService.shutdown();

        return newRentalList;
    }

    /**
     * 将消息添加至Redis消息队列中
     * @param rental
     */
    public void insertRedisStream(Rental rental) {
        // 创建消息记录, 以及指定stream
        StringRecord stringRecord = StreamRecords.string(Collections.singletonMap("rentalData", JSON.toJSONString(rental)))
                .withStreamKey(RENTAL_REDIS_KEY_TEMPLATE)
                .withId(RecordId.autoGenerate());
        // 将消息添加至消息队列中  另开个线程去更新数据  不占用查询线程的时间
        stringRedisTemplate.opsForStream().add(stringRecord);
    }

    /**
     * 创建租赁信息
     * @param rental
     * @return
     */
    @Transactional
    public ResponseDTO<Boolean> createRental(Rental rental) {
        // 根据体育器材id和租借数量判断体育器材剩余库存
        Equipment equipment = equipmentMapper.selectByPrimaryKey(rental.getEquipmentId());
        if(EquipmentStateEnum.OFF.getCode().equals(equipment.getState())) {
            return ResponseDTO.errorByMsg(CodeMsg.EQUIPMENT_ALREADY_OFF);
        }
        if(equipment.getNum() < rental.getNum()) {
            return ResponseDTO.errorByMsg(CodeMsg.EQUIPMENT_STOCK_ERROR);
        }
        // 数据落地
        if(rentalMapper.insertSelective(rental) == 0) {
            return ResponseDTO.errorByMsg(CodeMsg.RENTAL_ADD_ERROR);
        }
        // 减少体育器材数量
        myEquipmentMapper.decreaseRentalNum(rental.getNum(), rental.getEquipmentId());
        return ResponseDTO.successByMsg(true, "保存成功！");
    }

    /**
     * 删除租借数据
     * @param rentalDTO
     * @return
     */
    @Override
    @Transactional
    public ResponseDTO<Boolean> removeRental(RentalDTO rentalDTO) {
        if(CommonUtil.isEmpty(rentalDTO.getId())) {
            return ResponseDTO.errorByMsg(CodeMsg.DATA_ERROR);
        }
        if(rentalMapper.deleteByPrimaryKey(rentalDTO.getId()) == 0) {
            return ResponseDTO.errorByMsg(CodeMsg.RENTAL_DELETE_ERROR);
        }
        return ResponseDTO.successByMsg(true, "删除成功！");
    }

    /**
     * 获取租借总数
     * @return
     */
    @Override
    public ResponseDTO<Integer> getRentalTotal(UserDTO userDTO) {
        if(userDTO == null || CommonUtil.isEmpty(userDTO.getToken())){
            return ResponseDTO.errorByMsg(CodeMsg.USER_SESSION_EXPIRED);
        }
        ResponseDTO<UserDTO> responseDTO = userService.getLoginUser(userDTO.getToken());
        if(responseDTO.getCode() != 0 ) {
            return ResponseDTO.errorByMsg(CodeMsg.USER_SESSION_EXPIRED);
        }
        User user = userMapper.selectByPrimaryKey(responseDTO.getData().getId());
        RentalExample rentalExample = new RentalExample();
        if(RoleEnum.USER.getCode().equals(user.getRoleId())) {
            rentalExample.createCriteria().andUserIdEqualTo(user.getId());
        }
        return ResponseDTO.success(rentalMapper.countByExample(rentalExample));
    }

    /**
     * 获取近五天租借数
     * @return
     */
    @Override
    public ResponseDTO<List<Integer>> getRentalTotalByDays(UserDTO userDTO) {
        if(userDTO == null || CommonUtil.isEmpty(userDTO.getToken())){
            return ResponseDTO.errorByMsg(CodeMsg.USER_SESSION_EXPIRED);
        }
        ResponseDTO<UserDTO> responseDTO = userService.getLoginUser(userDTO.getToken());
        if(responseDTO.getCode() != 0 ) {
            return ResponseDTO.errorByMsg(CodeMsg.USER_SESSION_EXPIRED);
        }
        List<Integer> totalList = new ArrayList<>();
        Map<String, Object> queryMap = new HashMap<>();
        User user = userMapper.selectByPrimaryKey(responseDTO.getData().getId());
        if(RoleEnum.USER.getCode().equals(user.getRoleId())) {
            queryMap.put("userId", user.getId());
        }
        // 获取大大前天租借次数
        queryMap.put("start", 4);
        queryMap.put("end", 3);
        totalList.add(myRentalMapper.getRentalTotalByDate(queryMap));
        // 获取大前天租借次数
        queryMap.put("start", 3);
        queryMap.put("end", 2);
        totalList.add(myRentalMapper.getRentalTotalByDate(queryMap));
        // 获取前天租借次数
        queryMap.put("start", 2);
        queryMap.put("end", 1);
        totalList.add(myRentalMapper.getRentalTotalByDate(queryMap));
        // 获取昨天租借次数
        queryMap.put("start", 1);
        queryMap.put("end", 0);
        totalList.add(myRentalMapper.getRentalTotalByDate(queryMap));
        // 获取当天租借次数
        queryMap.put("start", 0);
        queryMap.put("end", -1);
        totalList.add(myRentalMapper.getRentalTotalByDate(queryMap));
        return ResponseDTO.success(totalList);
    }
}
