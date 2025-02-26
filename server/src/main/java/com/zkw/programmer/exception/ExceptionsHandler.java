package com.zkw.programmer.exception;


import com.zkw.programmer.bean.CodeMsg;
import com.zkw.programmer.dto.ResponseDTO;
import com.zkw.programmer.utils.CommonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;



/**
 * 运行时触发异常捕获
 */
@ControllerAdvice
public class ExceptionsHandler {

    private  final Logger logger = LoggerFactory.getLogger(ExceptionsHandler.class);

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseDTO<Boolean> handle(Exception e) {
        e.printStackTrace();
        if(!CommonUtil.isEmpty(e.getMessage())) {
            logger.info("异常信息={}", e.getMessage());
        }
        return ResponseDTO.errorByMsg(CodeMsg.SYSTEM_ERROR);
    }

}
