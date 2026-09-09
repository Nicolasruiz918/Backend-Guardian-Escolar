package com.guardianescolar.api.modules.dashboard.controller;

import com.guardianescolar.api.modules.dashboard.dto.DashboardSummaryResponse;
import com.guardianescolar.api.modules.dashboard.dto.HistoryReportResponse;
import com.guardianescolar.api.modules.dashboard.service.DashboardService;
import com.guardianescolar.api.shared.security.UserPrincipal;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DashboardController {

    private final DashboardService service;

    public DashboardController(DashboardService service) {
        this.service = service;
    }

    @GetMapping("/dashboard/summary")
    public DashboardSummaryResponse summary(@AuthenticationPrincipal UserPrincipal principal) {
        return service.summary(principal.id());
    }

    @GetMapping("/reports/history")
    public HistoryReportResponse historyReport(@AuthenticationPrincipal UserPrincipal principal) {
        return service.historyReport(principal.id());
    }
}
