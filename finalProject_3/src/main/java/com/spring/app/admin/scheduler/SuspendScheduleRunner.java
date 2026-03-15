package com.spring.app.admin.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.spring.app.admin.service.AdminService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class SuspendScheduleRunner {

    private final AdminService adminService;

    /**
     * 매일 새벽 2시 30분: 예약된 정지 처리
     * - SUSPEND_SCHEDULE 테이블에서 SCHEDULED_AT <= SYSDATE 인 항목을 가져와
     *   MEMBER.SUSPENDED=1 (일시정지) 또는 MEMBER.STATUS=0 (영구정지) 적용 후 삭제
     */
    @Scheduled(cron = "0 30 2 * * ?")
    public void processScheduledSuspensions() {
        log.info("[정지 예약 배치] 시작");
        try {
            adminService.processScheduledSuspensions();
            log.info("[정지 예약 배치] 완료");
        } catch (Exception e) {
            log.error("[정지 예약 배치] 오류 발생: {}", e.getMessage(), e);
        }
    }
}
