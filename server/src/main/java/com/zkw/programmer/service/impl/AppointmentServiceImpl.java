package com.zkw.programmer.service.impl;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zkw.programmer.bean.CodeMsg;
import com.zkw.programmer.dao.AppointmentMapper;
import com.zkw.programmer.dao.HallMapper;
import com.zkw.programmer.dao.UserMapper;
import com.zkw.programmer.dao.my.MyAppointmentMapper;
import com.zkw.programmer.domain.Appointment;
import com.zkw.programmer.domain.AppointmentExample;
import com.zkw.programmer.domain.Hall;
import com.zkw.programmer.domain.User;
import com.zkw.programmer.dto.AppointmentDTO;
import com.zkw.programmer.dto.PageDTO;
import com.zkw.programmer.dto.ResponseDTO;
import com.zkw.programmer.dto.UserDTO;
import com.zkw.programmer.enums.AppointmentStateEnum;
import com.zkw.programmer.enums.HallStateEnum;
import com.zkw.programmer.enums.RoleEnum;
import com.zkw.programmer.service.IAppointmentService;
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
@Transactional
public class AppointmentServiceImpl implements IAppointmentService {

    @Resource
    private AppointmentMapper appointmentMapper;

    @Resource
    private MyAppointmentMapper myAppointmentMapper;

    @Resource
    private UserMapper userMapper;

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private HallMapper hallMapper;

    @Resource
    private IUserService userService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    //redis键名模板
    private final static String HALL_REDIS_KEY_TEMPLATE = "hall_%s";
    private final static String APPOINTMENT_REDIS_KEY_TEMPLATE = "stream.appointment";

    private static final Logger logger = LoggerFactory.getLogger(AppointmentServiceImpl.class);

    private static final ExecutorService APPOINTMENT_UPDATE_EXECUTOR = Executors.newSingleThreadExecutor();


    @PostConstruct
    private void init() {
        APPOINTMENT_UPDATE_EXECUTOR.submit(new AppointmentHandler());
    }


    private class AppointmentHandler implements Runnable {

        @Override
        public void run() {
            while (true) {
                try {
                    // 1.获取消息队列中的预约信息 XREAD GROUP g1 c1 COUNT 1 BLOCK 2000 STREAMS stream.orders >
                    // ReadOffset.lastConsumed() 获取下一个未消费的预约数据
                    List<MapRecord<String, Object, Object>> list = stringRedisTemplate.opsForStream().read(
                            Consumer.from("g1", "c1"),
                            StreamReadOptions.empty().count(1).block(Duration.ofSeconds(2)),
                            StreamOffset.create(APPOINTMENT_REDIS_KEY_TEMPLATE, ReadOffset.lastConsumed())
                    );
                    // 2.判断预约信息是否为空
                    if (list == null || list.isEmpty()) {
                        // 如果为null，说明没有消息，继续下一次循环
                        continue;
                    }
                    // 解析数据 获取一条数据  因为上面count(1)指定获取一条
                    MapRecord<String, Object, Object> record = list.get(0);
                    Map<Object, Object> value = record.getValue();
                    String jsonAppointmentString = (String) value.get("appointmentData");
                    Appointment appointment = JSON.parseObject(jsonAppointmentString, Appointment.class);
                    // 3.更新预约数据
                    logger.info("接收到消息队列数据，准备更新...");
                    appointmentMapper.updateByPrimaryKeySelective(appointment);
                    logger.info("预约数据更新完成...");
                    // 4.确认消息 XACK
                    stringRedisTemplate.opsForStream().acknowledge(APPOINTMENT_REDIS_KEY_TEMPLATE, "g1", record.getId());
                } catch (Exception e) {
//                    logger.error("处理预约数据异常", e);
                    handlePendingList();
                }
            }
        }

        // 确认异常的预约数据再次处理
        private void handlePendingList() {
            while (true) {
                try {
                    // 1.获取pending-list中的预约信息 XREAD GROUP g1 c1 COUNT 1 BLOCK 2000 STREAMS stream.orders 0
                    // ReadOffset.from("0") 从第一个消息开始
                    List<MapRecord<String, Object, Object>> list = stringRedisTemplate.opsForStream().read(
                            Consumer.from("g1", "c1"),
                            StreamReadOptions.empty().count(1),
                            StreamOffset.create(APPOINTMENT_REDIS_KEY_TEMPLATE, ReadOffset.from("0"))
                    );
                    // 2.判断预约信息是否为空
                    if (list == null || list.isEmpty()) {
                        // 如果为null，说明没有异常消息，结束循环
                        break;
                    }
                    // 解析数据
                    MapRecord<String, Object, Object> record = list.get(0);
                    Map<Object, Object> value = record.getValue();
                    String jsonAppointmentString = (String) value.get("appointmentData");
                    Appointment appointment = JSON.parseObject(jsonAppointmentString, Appointment.class);
                    // 3.更新预约数据
                    logger.info("接收到消息队列数据，准备更新...");
                    appointmentMapper.updateByPrimaryKeySelective(appointment);
                    logger.info("预约数据更新完成...");
                    // 4.确认消息 XACK
                    stringRedisTemplate.opsForStream().acknowledge(APPOINTMENT_REDIS_KEY_TEMPLATE, "g1", record.getId());
                } catch (Exception e) {
//                    logger.error("处理预约数据异常", e);
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
     * 分页获取预约数据
     * @param pageDTO
     * @return
     */
    @Override
    public ResponseDTO<PageDTO<AppointmentDTO>> getAppointmentListByPage(PageDTO<AppointmentDTO> pageDTO, String token) {
        ResponseDTO<UserDTO> loginUserResponse = userService.getLoginUser(token);
        if(!CodeMsg.SUCCESS.getCode().equals(loginUserResponse.getCode())) {
            return ResponseDTO.errorByMsg(CodeMsg.USER_SESSION_EXPIRED);
        }
        UserDTO loginUserDTO = loginUserResponse.getData();
        AppointmentExample appointmentExample = new AppointmentExample();
        // 判断是否进行关键字搜索
        AppointmentDTO searchAppointmentDTO = pageDTO.getSearchEntity();
        AppointmentExample.Criteria criteria = appointmentExample.createCriteria();
        if(!CommonUtil.isEmpty(searchAppointmentDTO.getId())) {
            criteria.andIdLike("%" + searchAppointmentDTO.getId() + "%");
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
        appointmentExample.setOrderByClause("create_time desc");
        PageHelper.startPage(pageDTO.getPage(), pageDTO.getSize());
        // 分页查出预约数据
        List<Appointment> appointmentList = appointmentMapper.selectByExample(appointmentExample);
        PageInfo<Appointment> pageInfo = new PageInfo<>(appointmentList);
        // 获取数据的总数
        pageDTO.setTotal(pageInfo.getTotal());
        // 多线程处理预约信息的状态
        try {
            appointmentList = operateAppointmentListState(appointmentList, pageDTO.getTotalPage(), pageDTO.getSize());
        } catch (Exception e) {
            logger.error(e.getMessage());
            return ResponseDTO.errorByMsg(CodeMsg.APPOINTMENT_OPERATE_ERROR);
        }
        // 将domain类型数据  转成 DTO类型数据
        List<AppointmentDTO> appointmentDTOList = CopyUtil.copyList(appointmentList, AppointmentDTO.class);
        for(AppointmentDTO appointmentDTO : appointmentDTOList) {
            User user = userMapper.selectByPrimaryKey(appointmentDTO.getUserId());
            appointmentDTO.setUserDTO(CopyUtil.copy(user, UserDTO.class));
        }
        pageDTO.setList(appointmentDTOList);
        return ResponseDTO.success(pageDTO);
    }

    /**
     * 保存预约数据(添加、修改)
     * @param appointmentDTO
     * @return
     */
    @Override
    public ResponseDTO<Boolean> saveAppointment(AppointmentDTO appointmentDTO) {
        // 进行统一表单验证
        CodeMsg validate = ValidateEntityUtil.validate(appointmentDTO);
        if(!validate.getCode().equals(CodeMsg.SUCCESS.getCode())){
            return ResponseDTO.errorByMsg(validate);
        }
        Appointment appointment = CopyUtil.copy(appointmentDTO, Appointment.class);
        ResponseDTO<Boolean> responseDTO = ResponseDTO.successByMsg(true, "保存成功！");
        if(CommonUtil.isEmpty(appointment.getId())){
            // id为空 说明是添加数据
            // 生成8位id
            appointment.setId(UuidUtil.getShortUuid());
            appointment.setCreateTime(new Date());
            appointment.setState(AppointmentStateEnum.WAIT.getCode());
            String redissonKey = String.format(HALL_REDIS_KEY_TEMPLATE, appointment.getHallId());
            RLock lock = redissonClient.getLock(redissonKey);
            //1.加锁  阻塞获取锁：获取不到一直循环尝试获取
            lock.lock();
            try {
                //  @Transactional 事务执行完后  再unlock释放锁
                //  为了避免锁在事务提交前释放，我们应该在事务外层使用锁。
                responseDTO = createAppointment(appointment);
            }catch (Exception e){
                logger.error(e.getMessage());
            }finally {
                //解锁
                lock.unlock();
            }
        } else {
            // id不为空 说明是修改数据
            // 修改数据库中数据
            responseDTO = updateAppointment(appointment);

        }
        return responseDTO;
    }

    /**
     * 删除预约数据
     * @param appointmentDTO
     * @return
     */
    @Override
    public ResponseDTO<Boolean> removeAppointment(AppointmentDTO appointmentDTO) {
        if(CommonUtil.isEmpty(appointmentDTO.getId())) {
            return ResponseDTO.errorByMsg(CodeMsg.DATA_ERROR);
        }
        if(appointmentMapper.deleteByPrimaryKey(appointmentDTO.getId()) == 0) {
            return ResponseDTO.errorByMsg(CodeMsg.APPOINTMENT_DELETE_ERROR);
        }
        return ResponseDTO.successByMsg(true, "删除成功！");
    }

    /**
     * 获取预约总数
     * @return
     */
    @Override
    public ResponseDTO<Integer> getAppointmentTotal(UserDTO userDTO) {
        if(userDTO == null || CommonUtil.isEmpty(userDTO.getToken())){
            return ResponseDTO.errorByMsg(CodeMsg.USER_SESSION_EXPIRED);
        }
        ResponseDTO<UserDTO> responseDTO = userService.getLoginUser(userDTO.getToken());
        if(responseDTO.getCode() != 0 ) {
            return ResponseDTO.errorByMsg(CodeMsg.USER_SESSION_EXPIRED);
        }
        User user = userMapper.selectByPrimaryKey(responseDTO.getData().getId());
        AppointmentExample appointmentExample = new AppointmentExample();
        if(RoleEnum.USER.getCode().equals(user.getRoleId())) {
            appointmentExample.createCriteria().andUserIdEqualTo(user.getId());
        }
        return ResponseDTO.success(appointmentMapper.countByExample(appointmentExample));
    }

    /**
     * 获取近五天预约数
     * @return
     */
    @Override
    public ResponseDTO<List<Integer>> getAppointmentTotalByDays(UserDTO userDTO) {
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
        // 获取大大前天预约次数
        queryMap.put("start", 4);
        queryMap.put("end", 3);
        totalList.add(myAppointmentMapper.getAppointmentTotalByDate(queryMap));
        // 获取大前天预约次数
        queryMap.put("start", 3);
        queryMap.put("end", 2);
        totalList.add(myAppointmentMapper.getAppointmentTotalByDate(queryMap));
        // 获取前天预约次数
        queryMap.put("start", 2);
        queryMap.put("end", 1);
        totalList.add(myAppointmentMapper.getAppointmentTotalByDate(queryMap));
        // 获取昨天预约次数
        queryMap.put("start", 1);
        queryMap.put("end", 0);
        totalList.add(myAppointmentMapper.getAppointmentTotalByDate(queryMap));
        // 获取当天预约次数
        queryMap.put("start", 0);
        queryMap.put("end", -1);
        totalList.add(myAppointmentMapper.getAppointmentTotalByDate(queryMap));
        return ResponseDTO.success(totalList);
    }

    /**
     * 获取已经预约的数据
     * @return
     */
    @Override
    public ResponseDTO<List<AppointmentDTO>> getAppointmentExistList() {
        PageDTO<AppointmentDTO> appointPageDTO = new PageDTO<>();
        appointPageDTO.setPage(1);
        appointPageDTO.setSize(10);
        PageHelper.startPage(appointPageDTO.getPage(), appointPageDTO.getSize());
        // 分页查出预约数据
        List<Appointment> appointmentList = appointmentMapper.selectByExample(new AppointmentExample());
        PageInfo<Appointment> pageInfoAppoint = new PageInfo<>(appointmentList);
        // 获取数据的总数
        appointPageDTO.setTotal(appointPageDTO.getTotal());
        // 多线程处理预约信息的状态
        try {
            operateAppointmentListState(appointmentList, appointPageDTO.getTotalPage(), appointPageDTO.getSize());
        } catch (Exception e) {
            logger.error(e.getMessage());
        }


        AppointmentExample appointmentExample = new AppointmentExample();
        List<Integer> stateList = Arrays.asList(AppointmentStateEnum.WAIT.getCode(), AppointmentStateEnum.USING.getCode(), AppointmentStateEnum.SUCCESS.getCode());
        appointmentExample.createCriteria().andStateIn(stateList);
        List<Appointment> appointList = appointmentMapper.selectByExample(appointmentExample);
        return ResponseDTO.success(CopyUtil.copyList(appointList, AppointmentDTO.class));
    }

    /**
     * 更新预约信息
     * @param appointment
     * @return
     */
    @Transactional
    public ResponseDTO<Boolean> updateAppointment(Appointment appointment) {
        if(appointmentMapper.updateByPrimaryKeySelective(appointment) == 0){
            return ResponseDTO.errorByMsg(CodeMsg.APPOINTMENT_EDIT_ERROR);
        }
        return ResponseDTO.successByMsg(true, "保存成功！");
    }

    /**
     * 创建预约信息
     * @param appointment
     * @return
     */
    @Transactional
    public ResponseDTO<Boolean> createAppointment(Appointment appointment) {
        // 数据落地
        Hall hall = hallMapper.selectByPrimaryKey(appointment.getHallId());
        if(HallStateEnum.REPAIR.getCode().equals(hall.getState())) {
            return ResponseDTO.errorByMsg(CodeMsg.HALL_NOT_NORMAL);
        }
        AppointmentExample appointmentExample = new AppointmentExample();
        appointmentExample.createCriteria()
                .andHallIdEqualTo(appointment.getHallId())
                .andStartTimeEqualTo(appointment.getStartTime())
                .andEndTimeEqualTo(appointment.getEndTime());
        if(appointmentMapper.selectByExample(appointmentExample).size() != 0) {
            return ResponseDTO.errorByMsg(CodeMsg.HALL_IS_APPOINTED);
        }
        if(appointmentMapper.insertSelective(appointment) == 0) {
            return ResponseDTO.errorByMsg(CodeMsg.APPOINTMENT_ADD_ERROR);
        }
        return ResponseDTO.successByMsg(true, "保存成功！");
    }



    /**
     * 多线程处理预约信息的状态
     * @param appointmentList
     * @param pages
     * @param pageSize
     * @return
     */
    @Transactional
    @Override
    public List<Appointment> operateAppointmentListState(List<Appointment> appointmentList, Integer pages, Integer pageSize) throws InterruptedException {
        // 多线程更改预约信息的状态
        ExecutorService executorService = Executors.newFixedThreadPool(pages);
        CountDownLatch countDownLatch = new CountDownLatch(pages);
        List<Appointment> newAppointmentList = new ArrayList<>();
        for (int i = 0; i < pages; i++) {
            int finalI = i;
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    List<Appointment> subAppointmentList = appointmentList.stream().skip((finalI) * pageSize).limit(pageSize).collect(Collectors.toList());
                    for (Appointment appointment : subAppointmentList) {
                        if(AppointmentStateEnum.SUCCESS.getCode().equals(appointment.getState())) {
                            // 如果已经审核通过，状态自动改为已完成或者使用中
                            if(new Date().getTime() >= appointment.getEndTime().getTime()) {
                                appointment.setState(AppointmentStateEnum.FINISH.getCode());
                                // 创建消息记录, 以及指定stream
                                insertRedisStream(appointment);
                            } else if(new Date().getTime() >= appointment.getStartTime().getTime()) {
                                appointment.setState(AppointmentStateEnum.USING.getCode());
                                // 创建消息记录, 以及指定stream
                                insertRedisStream(appointment);
                            }
                        } else if (AppointmentStateEnum.USING.getCode().equals(appointment.getState())) {
                            // 如果是在使用中，状态自动改为已完成
                            if(new Date().getTime() > appointment.getEndTime().getTime()) {
                                appointment.setState(AppointmentStateEnum.FINISH.getCode());
                                // 创建消息记录, 以及指定stream
                                insertRedisStream(appointment);
                            }
                        }
                        newAppointmentList.add(appointment);
                    }

                    countDownLatch.countDown();
                }
            });
        }
        countDownLatch.await();

        executorService.shutdown();

        return newAppointmentList;
    }

    /**
     * 将消息添加至Redis消息队列中
     * @param appointment
     */
    public void insertRedisStream(Appointment appointment) {
        // 创建消息记录, 以及指定stream
        StringRecord stringRecord = StreamRecords.string(Collections.singletonMap("appointmentData", JSON.toJSONString(appointment)))
                .withStreamKey(APPOINTMENT_REDIS_KEY_TEMPLATE)
                .withId(RecordId.autoGenerate());
        // 将消息添加至消息队列中  另开个线程去更新数据  不占用查询线程的时间
        stringRedisTemplate.opsForStream().add(stringRecord);
    }



}
