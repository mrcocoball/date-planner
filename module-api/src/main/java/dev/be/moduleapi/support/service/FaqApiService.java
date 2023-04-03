package dev.be.moduleapi.support.service;

import dev.be.moduleapi.support.dto.FaqCategoryDto;
import dev.be.moduleapi.support.dto.FaqDto;
import dev.be.modulecore.repositories.support.FaqCategoryRepository;
import dev.be.modulecore.repositories.support.FaqRepository;
import dev.be.modulecore.service.PaginationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.stream.Collectors;

@Slf4j(topic = "SERVICE")
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class FaqApiService {

    private final FaqRepository faqRepository;
    private final FaqCategoryRepository faqCategoryRepository;
    private final PaginationService paginationService;


    public Page<FaqCategoryDto> getFaqCategoryList(Pageable pageable) {

        return paginationService.listToPage(faqCategoryRepository.findAll().stream().map(FaqCategoryDto::from).collect(Collectors.toList()), pageable);

    }

    public FaqCategoryDto getFaqCategory(Long id) {

        return faqCategoryRepository.findById(id).map(FaqCategoryDto::from).orElseThrow(EntityNotFoundException::new);

    }

    public FaqDto getFaq(Long id) {

        return faqRepository.findById(id).map(FaqDto::from).orElseThrow(EntityNotFoundException::new);

    }

}