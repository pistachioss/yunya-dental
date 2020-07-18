package com.yunya.feign.system.feign;

import com.yunya.feign.system.form.CompanyEditForm;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.Map;

@FeignClient(name = "yunya-system")
public interface OrganizationFeign {

    @PutMapping("/organization/edit/credit")
    Map<String, Object> updateCompanyCredit(CompanyEditForm editForm);
}
