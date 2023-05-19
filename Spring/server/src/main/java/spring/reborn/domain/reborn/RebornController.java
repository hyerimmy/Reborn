package spring.reborn.domain.reborn;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import spring.reborn.config.BaseException;
import spring.reborn.config.BaseResponse;
import spring.reborn.domain.awsS3.AwsS3Service;
import spring.reborn.domain.reborn.model.*;

import java.util.List;
import static spring.reborn.config.BaseResponseStatus.*;

@RestController
@RequestMapping("/reborns")
public class RebornController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final RebornProvider rebornProvider;
    @Autowired
    private final RebornService rebornService;

    @Autowired
    private final AwsS3Service awsS3Service;

    public RebornController(RebornProvider rebornProvider, RebornService rebornService, AwsS3Service awsS3Service) {
        this.rebornProvider = rebornProvider;
        this.rebornService = rebornService;
        this.awsS3Service = awsS3Service;
    }

    /* 상품 생성 */
    @ResponseBody
    @PostMapping(value = "/create/atOnce", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public BaseResponse<PostRebornRes> createAtOnceReborn(@RequestPart(required = false) PostRebornReq postRebornReq,
                                                    @RequestParam(name = "images") List<MultipartFile> multipartFile) {
        try {
            List<String> fileUrlList = awsS3Service.uploadImage(multipartFile);

            if (fileUrlList.size() >= 1) {
                postRebornReq.setProductImg(fileUrlList.get(0));
            }
            PostRebornRes postRebornRes = rebornService.createReborn(postRebornReq);
            return new BaseResponse<>(postRebornRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }


    /* 상품 생성 */
    @ResponseBody
    @PostMapping("/create")
    public BaseResponse<PostRebornRes> createReborn(@RequestBody PostRebornReq postRebornReq) {
        try {
            PostRebornRes postRebornRes = rebornService.createReborn(postRebornReq);
            return new BaseResponse<>(postRebornRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }


    /* 전체 리본 조회 (스토어) */
    @ResponseBody
    @GetMapping("/store/{storeIdx}/status")
    public BaseResponse<List<GetRebornRes>> getReborns(@PathVariable Integer storeIdx, @RequestParam String status) {
        try {
            List<GetRebornRes> getRebornsRes= rebornProvider.getReborns(storeIdx, status);
            return new BaseResponse<>(getRebornsRes);
        } catch (BaseException baseException) {
            return new BaseResponse<>(baseException.getStatus());
        }
    }

    /* 리본 조회 페이지 with 상태별 (스토어) */
    @ResponseBody
    @GetMapping("/store/page/{storeIdx}/status")
    public BaseResponse<List<GetRebornPageRes>> getRebornsPage(@PathVariable Integer storeIdx, @RequestParam String status) {
        try {
            List<GetRebornPageRes> getRebornsPageRes= rebornProvider.getRebornsPage(storeIdx, status);
            return new BaseResponse<>(getRebornsPageRes);
        } catch (BaseException baseException) {
            return new BaseResponse<>(baseException.getStatus());
        }
    }

    /* 진행 중인 리본 조회 (유저) */
    @ResponseBody
    @GetMapping("/inprogress/user/{userIdx}")
    public BaseResponse<List<GetInProgressRes>> getInProgressReborns(@PathVariable Integer userIdx) {
        try {
            List<GetInProgressRes> getInProgressRebornsRes = rebornProvider.getInProgressReborns(userIdx);
            return new BaseResponse<>(getInProgressRebornsRes);
        } catch (BaseException baseException) {
            return new BaseResponse<>(baseException.getStatus());
        }
    }

    /* 상품 수정 */
    @ResponseBody
    @PostMapping("/modify")
    public BaseResponse<String> patchReborn(@RequestBody PatchRebornReq patchRebornReq) {
        try {
//            if (patchRebornReq.getProductName() == null)
//                return new BaseResponse<>(PATCH_REBORN_EMPTY_PRODUCTNAME);
//            if (patchRebornReq.getProductGuide() == null)
//                return new BaseResponse<>(PATCH_REBORN_EMPTY_PRODUCTGUIDE);
//            if (patchRebornReq.getProductComment() == null)
//                return new BaseResponse<>(PATCH_REBORN_EMPTY_PRODUCTCOMMENT);
            String result = rebornService.patchReborn(patchRebornReq);
            return new BaseResponse<>(result);
        } catch (BaseException baseException) {
            return new BaseResponse<>(baseException.getStatus());
        }
    }

    /* 리본 히스토리 조회(유저) */
    @ResponseBody
    @GetMapping("/history/{userIdx}")
    public BaseResponse<List<GetHistoryRes>> getHistory(@PathVariable Integer userIdx) {
        try {
            List<GetHistoryRes> getHistoriesRes = rebornProvider.getHistory(userIdx);
            return new BaseResponse<>(getHistoriesRes);
        } catch (BaseException baseException) {
            return new BaseResponse<>(baseException.getStatus());
        }
    }

    /* 리본 히스토리 상세조회*/
    @ResponseBody
    @GetMapping("/history/detail/{rebornTaskIdx}")
    public BaseResponse<GetHistroyDetailRes> getHistoryDetail(@PathVariable Integer rebornTaskIdx) {
        try {
            GetHistroyDetailRes getHistroyDetailRes = rebornProvider.getHistoryDetail(rebornTaskIdx);
            return new BaseResponse<>(getHistroyDetailRes);
        } catch (BaseException baseException) {
            return new BaseResponse<>(baseException.getStatus());
        }
    }

    /* 리본 히스토리 생성 */
    @ResponseBody
    @PostMapping("/create/history/{rebornTaskIdx}")
    public BaseResponse<String> postHistory(@PathVariable int rebornTaskIdx) {
        try {
            if (rebornService.postHistory(rebornTaskIdx) == 1) {
                System.out.println("성공!");
            };
            String result = "히스토리 생성에 성공하였습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException baseException) {
            return new BaseResponse<>(baseException.getStatus());
        }
    }

    /* 상품 삭제 */
    @ResponseBody
    @PostMapping("/delete/{rebornIdx}")
    public BaseResponse<String> deleteProduct(@PathVariable int rebornIdx) {
        try {
            String result = rebornService.deleteProduct(rebornIdx);
            return new BaseResponse<>(result);
        } catch (BaseException baseException) {
            return new BaseResponse<>(baseException.getStatus());
        }
    }

    /* 나눔 취소 */
    @ResponseBody
    @PostMapping("/task/inactive/{rebornTaskIdx}")
    public BaseResponse<String> inactiveRebornTask(@PathVariable int rebornTaskIdx) {
        try {
            String result = rebornService.inactiveRebornTask(rebornTaskIdx);
            return new BaseResponse<>(result);
        } catch (BaseException baseException) {
            return new BaseResponse<>(baseException.getStatus());
        }
    }

    /* 리본상품 활성화, 비활성화 */
    @ResponseBody
    @PostMapping("/active/{rebornIdx}")
    public BaseResponse<PatchRebornStatusRes> activeReborn(@PathVariable int rebornIdx) throws BaseException {
        try {
            PatchRebornStatusRes result = rebornService.ativeReborn(rebornIdx);
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            throw new BaseException(exception.getStatus());
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
