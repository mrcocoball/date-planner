package dev.be.moduleadmin.place.controller;

import dev.be.moduleadmin.api.model.ListResult;
import dev.be.moduleadmin.api.model.SingleResult;
import dev.be.moduleadmin.api.service.ResponseService;
import dev.be.moduleadmin.place.dto.PlaceCrawlingDto;
import dev.be.moduleadmin.place.service.PlaceCrawlingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j(topic = "CONTROLLER")
@PreAuthorize("hasRole('ROLE_ADMIN')")
@RequiredArgsConstructor
@RestController
public class PlaceCrawlingApiController {

    private final PlaceCrawlingService placeCrawlingService;
    private final ResponseService responseService;

    @PostMapping("/api/v1/admin/crawlingAndUpdate")
    public SingleResult<Integer> crawlingAndUpdatePlacesV1() {

        return responseService.getSingleResult(placeCrawlingService.searchAndCrawlingV1().size());
    }

    @PostMapping("/api/v1/admin/crawlingAndUpdateLimit")
    public SingleResult<Integer> crawlingAndUpdatePlacesLimitV1() {

        return responseService.getSingleResult(placeCrawlingService.searchAndCrawlingLimitV1().size());
    }

}
