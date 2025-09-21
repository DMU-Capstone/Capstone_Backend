package com.waitit.capstone.domain.notice.service;

import com.waitit.capstone.domain.notice.dto.NoticeRequest;
import com.waitit.capstone.domain.notice.entity.Notice;
import com.waitit.capstone.domain.notice.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;

    @Transactional
    public void createNotice(NoticeRequest noticeRequest) {
        Notice notice = Notice.builder()
                .title(noticeRequest.getTitle())
                .content(noticeRequest.getContent())
                .build();
        noticeRepository.save(notice);
    }
}
