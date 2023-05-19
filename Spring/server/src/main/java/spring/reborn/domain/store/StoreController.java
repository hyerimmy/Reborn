package spring.reborn.domain.store;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import spring.reborn.config.BaseException;
import spring.reborn.config.BaseResponse;
import spring.reborn.config.BaseResponseStatus;
import spring.reborn.domain.store.model.*;
import spring.reborn.utils.JwtService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/store")
@RequiredArgsConstructor
public class StoreController {

    private final StoreService storeService;

    private final StoreProvider storeProvider;

    private final JwtService jwtService;

    /*
    가게 리스트 조회(업데이트 순)
     */
    @GetMapping("/list")
    public BaseResponse<List<GetStoreRes>> getStoreList() {
        try {
            List<GetStoreRes> getStoreResList = storeService.getStoreList();
            return new BaseResponse<>(getStoreResList);
        } catch (BaseException e) {
            log.error(e.getStatus().getMessage());
            return new BaseResponse<>((e.getStatus()));
        }

    }

    @GetMapping("/new")
    public BaseResponse<List<GetNewStoreRes>> getNewStoreList() {
        try {
            List<GetNewStoreRes> newStoreList = storeService.getNewStoreList();
            return new BaseResponse<>(newStoreList);
        } catch (BaseException e) {
            log.error(e.getStatus().getMessage());
            return new BaseResponse<>((e.getStatus()));
        }

    }

    /*
    가게 위치 표시
     */
    @GetMapping("/{storeIdx}/location")
    public BaseResponse<GetStoreLocationRes> getStoreLocation(@PathVariable Long storeIdx) {

        try {
            GetStoreLocationRes getStoreLocationRes = storeService.getStoreLocation(storeIdx);
            return new BaseResponse<>(getStoreLocationRes);
        } catch (BaseException e) {
            log.error(e.getStatus().getMessage());
            return new BaseResponse<>((e.getStatus()));
        }

    }

    /*
    가게 정보 조회
     */
    @GetMapping("/{storeIdx}")
    public BaseResponse<GetStoreInfoRes> getStoreInfo(@PathVariable Long storeIdx) {
        try {
            GetStoreInfoRes getStoreRes = storeService.getStoreInfo(storeIdx);
            return new BaseResponse<>(getStoreRes);
        } catch (BaseException e) {
            log.error(e.getStatus().getMessage());
            return new BaseResponse<>(e.getStatus());
        }

    }


    /*
    가게 검색
     */
    @GetMapping("/search")
    public BaseResponse<List<GetStoreRes>> searchStore(@RequestParam(required = false) String keyword,
                                                       @RequestParam(required = false) String sort) {
        try {
            if (keyword == null || keyword.isEmpty())
                throw new BaseException(BaseResponseStatus.GET_STORE_EMPTY_KEYWORD);

            List<GetStoreRes> getStoreRes;

            if (sort == null || sort.equals("name")) {
                getStoreRes = storeService.searchStoreListUsingTitleSortByName(keyword);

            } else if (sort.toUpperCase().equals("score".toUpperCase())) {
                getStoreRes = storeService.searchStoreListUsingTitleSortByScore(keyword);

            } else if (sort.toUpperCase().equals("jjim".toUpperCase())) {
                getStoreRes = storeService.searchStoreListUsingTitleSortByJjim(keyword);

            }
            // 잘못된 정렬 값도 이름순 처리
            else {
                getStoreRes = storeService.searchStoreListUsingTitleSortByName(keyword);
            }

            return new BaseResponse<>(getStoreRes);
        } catch (BaseException e) {
            log.error(e.getStatus().getMessage());
            return new BaseResponse<>(e.getStatus());
        }
    }

    /*
    가게 정보 수정
    patch -> post
     */
    // string으로 url주소 삽입
    @PostMapping("/update/{storeIdx}")
    public BaseResponse<Object> updateStoreInfo(@PathVariable Long storeIdx,
                                                @RequestBody PatchStoreReq patchStoreReq) {
        try {
            if (patchStoreReq.getStoreName().isEmpty())
                throw new BaseException(BaseResponseStatus.MODIFY_FAIL_STORE_EMPTY_NAME);
            if (patchStoreReq.getStoreAddress().isEmpty())
                throw new BaseException(BaseResponseStatus.MODIFY_FAIL_STORE_EMPTY_LOCATION);
            storeService.updateStoreInfo(storeIdx, patchStoreReq);


            return new BaseResponse<>(new PatchStoreRes(storeIdx));
        } catch (BaseException e) {
            log.error(e.getStatus().getMessage());
            return new BaseResponse<>(e.getStatus());
        }
    }
    // 사진 multipart/form으로 올리기
    @PostMapping(value = "/update2/{storeIdx}",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE}
    )
    public BaseResponse<Object> updateStoreInfo2(@PathVariable Long storeIdx,
                                                @RequestPart PatchStoreReq patchStoreReq,
                                                @RequestPart(name = "image", required = false) MultipartFile multipartFile) {
        try {
            if (patchStoreReq.getStoreName().isEmpty())
                throw new BaseException(BaseResponseStatus.MODIFY_FAIL_STORE_EMPTY_NAME);
            if (patchStoreReq.getStoreAddress().isEmpty())
                throw new BaseException(BaseResponseStatus.MODIFY_FAIL_STORE_EMPTY_LOCATION);

            if (multipartFile == null)
                storeService.updateStoreInfo2(storeIdx, patchStoreReq);
            else
                storeService.updateStoreInfo2(storeIdx, patchStoreReq, multipartFile);

            return new BaseResponse<>(new PatchStoreRes(storeIdx));
        } catch (BaseException e) {
            log.error(e.getStatus().getMessage());
            return new BaseResponse<>(e.getStatus());
        }
    }



    /* 인기가게 조회 */
    @ResponseBody
    @GetMapping("/popular")
    public BaseResponse<List<GetPopularStoreRes>> getPopularStore(@RequestParam String category) {
        try {
            List<GetPopularStoreRes> getPopularStores = storeProvider.getPopularStore(category);
            return new BaseResponse<>(getPopularStores);
        } catch (BaseException baseException) {
            return new BaseResponse<>(baseException.getStatus());
        }
    }

    /**
     * 유저의 좋아할만한 가게 API
     * [GET]
     */
    @GetMapping("/likeable-stores")
    public BaseResponse<List<GetLikeableStoreRes>> getLikeableStores() {
        try {
            int userIdx = jwtService.getUserIdx();
            List<GetLikeableStoreRes> likeableStoreRes = storeService.getLikeableStores(userIdx);
            return new BaseResponse<>(likeableStoreRes);

        } catch (BaseException e) {
            e.printStackTrace();
            log.error(e.getStatus().getMessage());
            return new BaseResponse<>(e.getStatus());
        }

    }

}
