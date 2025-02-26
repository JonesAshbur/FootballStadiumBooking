package com.zkw.programmer.service.impl;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zkw.programmer.bean.CodeMsg;
import com.zkw.programmer.dao.AnnounceMapper;
import com.zkw.programmer.dao.AppointmentMapper;
import com.zkw.programmer.dao.RentalMapper;
import com.zkw.programmer.dao.UserMapper;
import com.zkw.programmer.domain.*;
import com.zkw.programmer.dto.PageDTO;
import com.zkw.programmer.dto.ResponseDTO;
import com.zkw.programmer.dto.UserDTO;
import com.zkw.programmer.enums.RoleEnum;
import com.zkw.programmer.service.IUserService;
import com.zkw.programmer.utils.CommonUtil;
import com.zkw.programmer.utils.CopyUtil;
import com.zkw.programmer.utils.UuidUtil;
import com.zkw.programmer.utils.ValidateEntityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;


@Service
@Transactional
public class UserServiceImpl implements IUserService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private AnnounceMapper announceMapper;

    @Resource
    private RentalMapper rentalMapper;

    @Resource
    private AppointmentMapper appointmentMapper;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    /**
     * 用户登录操作
     * @param userDTO
     * @return
     */
    @Override
    public ResponseDTO<UserDTO> login(UserDTO userDTO) {
        // 进行是否为空判断
        if(CommonUtil.isEmpty(userDTO.getUsername())){
            return ResponseDTO.errorByMsg(CodeMsg.USERNAME_EMPTY);
        }
        if(CommonUtil.isEmpty(userDTO.getPassword())){
            return ResponseDTO.errorByMsg(CodeMsg.PASSWORD_EMPTY);
        }
        if(CommonUtil.isEmpty(userDTO.getCaptcha())){
            return ResponseDTO.errorByMsg(CodeMsg.CAPTCHA_EMPTY);
        }
        if(CommonUtil.isEmpty(userDTO.getCorrectCaptcha())){
            return ResponseDTO.errorByMsg(CodeMsg.CAPTCHA_EXPIRED);
        }
        // 比对验证码是否正确
        String value = stringRedisTemplate.opsForValue().get((userDTO.getCorrectCaptcha()));
        if(CommonUtil.isEmpty(value)){
            return ResponseDTO.errorByMsg(CodeMsg.CAPTCHA_EXPIRED);
        }
        if(!value.toLowerCase().equals(userDTO.getCaptcha().toLowerCase())){
            return ResponseDTO.errorByMsg(CodeMsg.CAPTCHA_ERROR);
        }
        // 对比昵称和密码是否正确
        UserExample userExample = new UserExample();
        // select * from user where username = ? and password = ?
        userExample.createCriteria().andUsernameEqualTo(userDTO.getUsername()).andPasswordEqualTo(userDTO.getPassword());
        List<User> userList = userMapper.selectByExample(userExample);
        if(userList == null || userList.size() != 1){
            return ResponseDTO.errorByMsg(CodeMsg.USERNAME_PASSWORD_ERROR);
        }
        // 生成登录token并存入Redis中
        UserDTO selectedUserDTO = CopyUtil.copy(userList.get(0), UserDTO.class);
        String token = UuidUtil.getShortUuid();
        selectedUserDTO.setToken(token);
        //把token存入redis中 有效期1小时
        stringRedisTemplate.opsForValue().set("USER_" + token, JSON.toJSONString(selectedUserDTO), 3600, TimeUnit.SECONDS);
        return ResponseDTO.successByMsg(selectedUserDTO, "登录成功！");
    }

    /**
     * 获取当前登录用户
     * @param token
     * @return
     */
    @Override
    public ResponseDTO<UserDTO> getLoginUser(String token) {
        if(CommonUtil.isEmpty(token)){
            return ResponseDTO.errorByMsg(CodeMsg.USER_SESSION_EXPIRED);
        }
        String value = stringRedisTemplate.opsForValue().get("USER_" + token);
        if(CommonUtil.isEmpty(value)){
            return ResponseDTO.errorByMsg(CodeMsg.USER_SESSION_EXPIRED);
        }
        UserDTO selectedUserDTO = JSON.parseObject(value, UserDTO.class);
        return ResponseDTO.success(CopyUtil.copy(userMapper.selectByPrimaryKey(selectedUserDTO.getId()), UserDTO.class));
    }

    /**
     * 用户注册操作
     * @param userDTO
     * @return
     */
    @Override
    public ResponseDTO<Boolean> register(UserDTO userDTO) {
        if(userDTO == null){
            return ResponseDTO.errorByMsg(CodeMsg.DATA_ERROR);
        }
        // 进行统一表单验证
        CodeMsg validate = ValidateEntityUtil.validate(userDTO);
        if(!validate.getCode().equals(CodeMsg.SUCCESS.getCode())){
            return ResponseDTO.errorByMsg(validate);
        }
        // 判断重复密码是否正确
        if(CommonUtil.isEmpty(userDTO.getRePassword())){
            return ResponseDTO.errorByMsg(CodeMsg.REPASSWORD_EMPTY);
        }
        if(!userDTO.getPassword().equals(userDTO.getRePassword())){
            return ResponseDTO.errorByMsg(CodeMsg.REPASSWORD_ERROR);
        }
        User user = CopyUtil.copy(userDTO, User.class);
        // 判断用户昵称是否存在
        if(isUsernameExist(user, "")){
            return ResponseDTO.errorByMsg(CodeMsg.USERNAME_EXIST);
        }
        // 判断用户手机号是否存在
        if(isPhoneExist(user, "")){
            return ResponseDTO.errorByMsg(CodeMsg.USER_PHONE_EXIST);
        }
        // 设置主键id
        user.setId(UuidUtil.getShortUuid());
        // 角色为普通用户
        user.setRoleId(RoleEnum.USER.getCode());
        // 保存数据到数据库中
        if(userMapper.insertSelective(user) == 0){
            return ResponseDTO.errorByMsg(CodeMsg.USER_REGISTER_ERROR);
        }
        return ResponseDTO.successByMsg(true, "注册成功，快登录体验吧！");
    }

    /**
     * 检查用户是否登录
     * @param userDTO
     * @return
     */
    @Override
    public ResponseDTO<UserDTO> checkLogin(UserDTO userDTO) {
        if(userDTO == null || CommonUtil.isEmpty(userDTO.getToken())){
            return ResponseDTO.errorByMsg(CodeMsg.USER_SESSION_EXPIRED);
        }
        ResponseDTO<UserDTO> responseDTO = getLoginUser(userDTO.getToken());
        if(responseDTO.getCode() != 0 ) {
            return responseDTO;
        }
        User user = userMapper.selectByPrimaryKey(responseDTO.getData().getId());
        if(user == null) {
            logout(userDTO);
            return ResponseDTO.errorByMsg(CodeMsg.USER_NOT_EXIST);
        }
        if(responseDTO.getCode() != 0){
            return responseDTO;
        }
        logger.info("获取到的登录信息={}", responseDTO.getData());
        return ResponseDTO.success(responseDTO.getData());
    }

    /**
     * 退出登录操作
     * @param userDTO
     * @return
     */
    @Override
    public ResponseDTO<Boolean> logout(UserDTO userDTO) {
        if(!CommonUtil.isEmpty(userDTO.getToken())){
            // token不为空  清除redis中数据
            stringRedisTemplate.delete("USER_" + userDTO.getToken());
        }
        return ResponseDTO.successByMsg(true, "退出登录成功！");
    }

    /**
     * 获取用户总数
     * @return
     */
    @Override
    public ResponseDTO<Integer> getUserTotal() {
        return ResponseDTO.success(userMapper.countByExample(new UserExample()));
    }

    /**
     * 分页获取用户数据
     * @param pageDTO
     * @return
     */
    @Override
    public ResponseDTO<PageDTO<UserDTO>> getUserListByPage(PageDTO<UserDTO> pageDTO, String token) {
        ResponseDTO<UserDTO> loginUserResponse = getLoginUser(token);
        if(!CodeMsg.SUCCESS.getCode().equals(loginUserResponse.getCode())) {
            return ResponseDTO.errorByMsg(CodeMsg.USER_SESSION_EXPIRED);
        }
        UserDTO loginUserDTO = loginUserResponse.getData();
        UserExample userExample = new UserExample();
        // 判断是否进行关键字搜索
        UserDTO searchUserDTO = pageDTO.getSearchEntity();
        UserExample.Criteria criteria = userExample.createCriteria();
        if(!CommonUtil.isEmpty(searchUserDTO.getUsername())) {
            criteria.andUsernameLike("%" + searchUserDTO.getUsername() + "%");
        }
        if(searchUserDTO.getRoleId() != null && searchUserDTO.getRoleId() != 0) {
            criteria.andRoleIdEqualTo(searchUserDTO.getRoleId());
        }
        // 如果是普通用户，只能看到自己的信息
        if(RoleEnum.USER.getCode().equals(loginUserDTO.getRoleId())) {
            criteria.andIdEqualTo(loginUserDTO.getId());
        }
        // 不知道当前页多少，默认为第一页
        if(pageDTO.getPage() == null){
            pageDTO.setPage(1);
        }
        if(pageDTO.getSize() == null) {
            pageDTO.setSize(5);
        }
        PageHelper.startPage(pageDTO.getPage(), pageDTO.getSize());
        // 分页查出用户数据
        List<User> userList = userMapper.selectByExample(userExample);
        PageInfo<User> pageInfo = new PageInfo<>(userList);
        // 获取数据的总数
        pageDTO.setTotal(pageInfo.getTotal());
        // 将domain类型数据  转成 DTO类型数据
        List<UserDTO> userDTOList = CopyUtil.copyList(userList, UserDTO.class);
        pageDTO.setList(userDTOList);
        return ResponseDTO.success(pageDTO);
    }

    /**
     * 保存用户数据(添加、修改)
     * @param userDTO
     * @return
     */
    @Override
    public ResponseDTO<Boolean> saveUser(UserDTO userDTO, String token) {
        ResponseDTO<UserDTO> loginUserResponse = getLoginUser(token);
        if(!CodeMsg.SUCCESS.getCode().equals(loginUserResponse.getCode())) {
            return ResponseDTO.errorByMsg(CodeMsg.USER_SESSION_EXPIRED);
        }
        UserDTO loginUserDTO = loginUserResponse.getData();
        // 进行统一表单验证
        CodeMsg validate = ValidateEntityUtil.validate(userDTO);
        if(!validate.getCode().equals(CodeMsg.SUCCESS.getCode())){
            return ResponseDTO.errorByMsg(validate);
        }
        User user = CopyUtil.copy(userDTO, User.class);
        if(CommonUtil.isEmpty(user.getId())){
            // id为空 说明是添加数据
            // 判断用户昵称是否存在
            if(isUsernameExist(user, "")){
                return ResponseDTO.errorByMsg(CodeMsg.USERNAME_EXIST);
            }
            // 判断用户手机号是否存在
            if(isPhoneExist(user, "")){
                return ResponseDTO.errorByMsg(CodeMsg.USER_PHONE_EXIST);
            }
            // 生成8位id
            user.setId(UuidUtil.getShortUuid());
            // 添加数据到数据库
            if(userMapper.insertSelective(user) == 0){
                return ResponseDTO.errorByMsg(CodeMsg.USER_ADD_ERROR);
            }
        }else{
            // id不为空 说明是修改数据
            // 判断用户昵称是否存在
            if(isUsernameExist(user, user.getId())){
                return ResponseDTO.errorByMsg(CodeMsg.USERNAME_EXIST);
            }
            // 判断用户手机号是否存在
            if(isPhoneExist(user, user.getId())){
                return ResponseDTO.errorByMsg(CodeMsg.USER_PHONE_EXIST);
            }
            // 修改数据库中数据
            if(userMapper.updateByPrimaryKeySelective(user) == 0){
                return ResponseDTO.errorByMsg(CodeMsg.USER_EDIT_ERROR);
            }
            if(loginUserDTO.getId().equals(user.getId())) {
                stringRedisTemplate.opsForValue().set("USER_" + userDTO.getToken(), JSON.toJSONString(userMapper.selectByPrimaryKey(userDTO.getId())), 3600, TimeUnit.SECONDS);
            }
        }
        return ResponseDTO.successByMsg(true, "保存成功！");
    }

    /**
     * 删除用户数据
     * @param userDTO
     * @return
     */
    @Override
    public ResponseDTO<Boolean> removeUser(UserDTO userDTO) {
        if(CommonUtil.isEmpty(userDTO.getId())){
            return ResponseDTO.errorByMsg(CodeMsg.DATA_ERROR);
        }
        // 删除用户信息
        if(userMapper.deleteByPrimaryKey(userDTO.getId()) == 0){
            return ResponseDTO.errorByMsg(CodeMsg.USER_DELETE_ERROR);
        }
        // 删除用户有关的公告数
        AnnounceExample announceExample = new AnnounceExample();
        announceExample.createCriteria().andUserIdEqualTo(userDTO.getId());
        announceMapper.deleteByExample(announceExample);
        // 删除用户有关的租借数
        RentalExample rentalExample = new RentalExample();
        rentalExample.createCriteria().andUserIdEqualTo(userDTO.getId());
        rentalMapper.deleteByExample(rentalExample);
        // 删除用户有关的预约数
        AppointmentExample appointmentExample = new AppointmentExample();
        appointmentExample.createCriteria().andUserIdEqualTo(userDTO.getId());
        appointmentMapper.deleteByExample(appointmentExample);
        return ResponseDTO.successByMsg(true, "删除成功！");
    }

    /**
     * 判断用户昵称是否重复
     * @param user
     * @param id
     * @return
     */
    public Boolean isUsernameExist(User user, String id) {
        UserExample userExample = new UserExample();
        userExample.createCriteria().andUsernameEqualTo(user.getUsername());
        List<User> selectedUserList = userMapper.selectByExample(userExample);
        if(selectedUserList != null && selectedUserList.size() > 0) {
            if(selectedUserList.size() > 1){
                return true; //出现重名
            }
            if(!selectedUserList.get(0).getId().equals(id)) {
                return true; //出现重名
            }
        }
        return false;//没有重名
    }


    /**
     * 判断手机号是否重复
     * @param user
     * @param id
     * @return
     */
    public Boolean isPhoneExist(User user, String id) {
        UserExample userExample = new UserExample();
        userExample.createCriteria().andPhoneEqualTo(user.getPhone());
        List<User> selectedUserList = userMapper.selectByExample(userExample);
        if(selectedUserList != null && selectedUserList.size() > 0) {
            if(selectedUserList.size() > 1){
                return true; //出现重名
            }
            if(!selectedUserList.get(0).getId().equals(id)) {
                return true; //出现重名
            }
        }
        return false;//没有重名
    }
}
