package dev.be.moduleadmin.place.controller;

import dev.be.moduleadmin.place.dto.PlaceAdminDetailDto;
import dev.be.moduleadmin.place.dto.PlaceModifyRequestDto;
import dev.be.moduleadmin.place.dto.PlaceRequestDto;
import dev.be.moduleadmin.place.dto.PlaceStatusDto;
import dev.be.moduleadmin.place.service.PlaceAdminService;
import dev.be.modulecore.service.PaginationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@Slf4j(topic = "CONTROLLER")
@RequiredArgsConstructor
@Controller
@RequestMapping("/admin/places")
public class PlaceAdminController {

    private final PlaceAdminService placeAdminService;
    private final PaginationService paginationService;

    @GetMapping()
    public String getPlaceList(@RequestParam(required = false) String region1, @RequestParam(required = false) String region2, @RequestParam(required = false) String region3,
                               @RequestParam(required = false) String categoryId, @RequestParam(required = false) Long id,
                               @RequestParam(required = false) String placeId, @RequestParam(required = false) String placeName, @RequestParam(required = false) Long reviewCount,
                               @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                               @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate targetDate,
                               @PageableDefault(size = 50, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable, ModelMap map) {

        Page<PlaceAdminDetailDto> dtos = paginationService.listToPage(
                placeAdminService.getPlaceList(region1, region2, region3, categoryId, id, placeId, placeName, reviewCount, startDate, targetDate), pageable);
        List<Integer> pageBarNumbers = paginationService.getPaginationBarNumbers(pageable.getPageNumber(), dtos.getTotalPages());
        map.addAttribute("dtos", dtos);
        map.addAttribute("pageBarNumbers", pageBarNumbers);

        return "admin/places/places";
    }

    @GetMapping("/{id}")
    public String getPlace(@PathVariable("id") Long id, ModelMap map) {

        PlaceAdminDetailDto dto = placeAdminService.getPlace(id);
        map.addAttribute("dto", dto);

        return "admin/places/places_detail";
    }

    @GetMapping("/create")
    public String createPlaceForm() {

        return "admin/places/places_create";

    }

    @PostMapping("/create")
    public String createPlace(@Valid PlaceRequestDto dto,
                             BindingResult r,
                             RedirectAttributes ra) {

        if (r.hasErrors()) {

            log.info("[PlaceAdminController createPlace] validation error");
            ra.addFlashAttribute("errors", r.getAllErrors());

            return "redirect:/admin/places/create";

        }

        placeAdminService.savePlace(dto);

        return "redirect:/admin/places";

    }

    @GetMapping("/{id}/modify")
    public String modifyPlaceForm(@PathVariable("id") Long id, ModelMap map) {

        PlaceModifyRequestDto dto = PlaceModifyRequestDto.from(placeAdminService.getPlace(id));
        map.addAttribute("dto", dto);

        return "admin/places/places_modify";

    }

    @PostMapping("/{id}/modify")
    public String modifyPlace(@PathVariable("id") Long id,
                             @Valid PlaceModifyRequestDto dto,
                             BindingResult r,
                             RedirectAttributes ra) {

        if (r.hasErrors()) {

            log.info("[PlaceAdminController modifyPlace] validation error");
            ra.addFlashAttribute("errors", r.getAllErrors());

            return "redirect:/admin/places/" + id + "/modify";

        }

        placeAdminService.updatePlace(dto);

        return "redirect:/admin/places/" + id;

    }

    @PostMapping("/{id}/delete")
    public String deletePlace(@PathVariable("id") Long id) {

        placeAdminService.deletePlace(id);

        return "redirect:/admin/places";

    }

    /**
     * 장소 크롤링 관련
     */

    @GetMapping("/nullPlaces")
    public String getImageUrlNullPlacesV1(@PageableDefault(size = 50) Pageable pageable, ModelMap map) {

        Page<PlaceStatusDto> dtos =  paginationService.listToPage(placeAdminService.getImageUrlNullPlacesV1(), pageable);
        List<Integer> pageBarNumbers = paginationService.getPaginationBarNumbers(pageable.getPageNumber(), dtos.getTotalPages());
        map.addAttribute("dtos", dtos);
        map.addAttribute("pageBarNumbers", pageBarNumbers);

        return "admin/places/places_null";
    }

    @GetMapping("/notExistPlaces")
    public String getImageUrlNotExistPlacesV1(@PageableDefault(size = 50) Pageable pageable, ModelMap map) {

        Page<PlaceStatusDto> dtos = paginationService.listToPage(placeAdminService.getImageUrlNotExistPlacesV1(), pageable);
        List<Integer> pageBarNumbers = paginationService.getPaginationBarNumbers(pageable.getPageNumber(), dtos.getTotalPages());
        map.addAttribute("dtos", dtos);
        map.addAttribute("pageBarNumbers", pageBarNumbers);

        return "admin/places/places_notExist";
    }

}


