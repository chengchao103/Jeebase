package com.jeebase.system.security.controller;

import java.util.List;

import com.jeebase.common.base.Constant;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jeebase.common.annotation.log.AroundLog;
import com.jeebase.common.base.Result;
import com.jeebase.system.security.dto.CreateOrganization;
import com.jeebase.system.security.dto.UpdateOrganization;
import com.jeebase.system.security.entity.Organization;
import com.jeebase.system.security.service.IOrganizationService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

/**
 * <p>
 * 组织表 前端控制器
 * </p>
 *
 * @author jeebase
 * @since 2018-05-19
 */
@RestController
@RequestMapping("/organization")
@Api(value = "OrganizationController|组织机构相关的前端控制器")
public class OrganizationController {

    @Autowired
    private IOrganizationService organizationService;

    /**
     * 查询组织树
     * 
     * @param parentId
     * @return
     */
    @GetMapping(value = "/tree")
    @RequiresRoles("SYSADMIN")
    @ApiOperation(value = "查询组织机构树", notes = "树状展示组织机构信息")
    @ApiImplicitParam(paramType = "query", name = "parentId", value = "父级ID", required = false, dataType = "Integer")
    public Result<List<Organization>> queryOrganizationTree(Integer parentId) {
        List<Organization> treeList = organizationService.queryOrganizationByPanentId(parentId);
        return new Result<List<Organization>>().success().put(treeList);
    }

    /**
     * 添加组织
     */
    @PostMapping("/create")
    @RequiresRoles("SYSADMIN")
    @ApiOperation(value = "添加组织机构")
    @AroundLog(name = "添加组织机构")
    public Result<Organization> create(@RequestBody CreateOrganization org) {
        Organization orgEntity = new Organization();
        BeanCopier.create(CreateOrganization.class, Organization.class, false).copy(org, orgEntity, null);
        List<String> areas = org.getAreas();
        if (!CollectionUtils.isEmpty(areas)) {
            orgEntity.setProvince(areas.get(Constant.Address.PROVINCE));
            orgEntity.setCity(areas.get(Constant.Address.CITY));
            orgEntity.setArea(areas.get(Constant.Address.AREA));
        }
        boolean result = organizationService.save(orgEntity);
        if (result) {
            return new Result<Organization>().success("添加成功").put(orgEntity);
        } else {
            return new Result<Organization>().error("添加失败，请重试");
        }
    }

    /**
     * 修改组织
     */
    @PostMapping("/update")
    @RequiresRoles("SYSADMIN")
    @ApiOperation(value = "更新组织机构")
    @AroundLog(name = "更新组织机构")
    public Result<Organization> update(@RequestBody UpdateOrganization org) {
        Organization orgEntity = new Organization();
        BeanCopier.create(UpdateOrganization.class, Organization.class, false).copy(org, orgEntity, null);
        List<String> areas = org.getAreas();
        if (!CollectionUtils.isEmpty(areas)) {
            orgEntity.setProvince(areas.get(Constant.Address.PROVINCE));
            orgEntity.setCity(areas.get(Constant.Address.CITY));
            orgEntity.setArea(areas.get(Constant.Address.AREA));
        }
        boolean result = organizationService.updateById(orgEntity);
        if (result) {
            return new Result<Organization>().success("修改成功").put(orgEntity);
        } else {
            return new Result<Organization>().error("修改失败");
        }
    }

    /**
     * 删除组织
     */
    @RequiresRoles("SYSADMIN")
    @PostMapping("/delete/{orgId}")
    @ApiOperation(value = "删除组织机构")
    @AroundLog(name = "删除组织机构")
    @ApiImplicitParam(paramType = "path", name = "orgId", value = "组织机构ID", required = true, dataType = "Integer")
    public Result<?> delete(@PathVariable("orgId") Integer orgId) {
        QueryWrapper<Organization> wpd = new QueryWrapper<Organization>();
        wpd.and(e -> e.eq("id", orgId).or().eq("parent_id", orgId));
        boolean result = organizationService.remove(wpd);
        if (result) {
            return new Result<>().success("删除成功");
        } else {
            return new Result<>().error("删除失败");
        }
    }
}
