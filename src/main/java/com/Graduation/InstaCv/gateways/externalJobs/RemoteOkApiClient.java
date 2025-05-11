package com.Graduation.InstaCv.gateways.externalJobs;

import com.Graduation.InstaCv.data.dto.RemoteOkJobResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "RemoteOkJobsClient", url = "${external.jobs.api.remoteok}")
@Component
public interface RemoteOkApiClient {
    @GetMapping
    List<RemoteOkJobResponse> getRemoteOkJobs();
}
