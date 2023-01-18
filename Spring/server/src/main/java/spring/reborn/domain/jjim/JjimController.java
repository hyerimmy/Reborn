package spring.reborn.domain.jjim;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import spring.reborn.config.BaseException;
import spring.reborn.config.BaseResponse;
import spring.reborn.domain.jjim.model.JjimReq;
import spring.reborn.domain.jjim.model.JjimRes;


@RestController
public class JjimController {
    @Autowired
    private final JjimProvider jjimProvider;
    @Autowired
    private final JjimService jjimService;

    public JjimController(JjimProvider jjimProvider, JjimService jjimService) {
        this.jjimProvider = jjimProvider;
        this.jjimService = jjimService;
    }

    @ResponseBody
    @PostMapping("/jjim")
    public BaseResponse<JjimRes> createJjim(@RequestBody JjimReq jjimReq) {
        try {
            JjimRes jjimRes = jjimService.createJjim(jjimReq);

            return new BaseResponse<>(jjimRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    @ResponseBody
    @DeleteMapping("/jjim")
    public BaseResponse<JjimRes> deleteJjim(@RequestBody JjimReq jjimReq) {
        try {

            System.out.println("controller 시작");
            JjimRes jjimRes = jjimService.deleteJjim(jjimReq);

            System.out.println("controller 끝");
            return new BaseResponse<>(jjimRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }
}