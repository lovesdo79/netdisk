package com.ruoyi.disk.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.disk.domain.BackFilelist;
import com.ruoyi.disk.domain.DiskFile;
import com.ruoyi.disk.domain.DiskShareFile;
import com.ruoyi.disk.service.IBackFilelistService;
import com.ruoyi.disk.service.IDiskFileService;
import com.ruoyi.disk.service.IDiskShareFileService;
import com.ruoyi.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * 已上传文件列表Controller
 */
@RestController
@RequestMapping("/disk/backFilelist")
public class BackFilelistController extends BaseController {
    @Autowired
    private IBackFilelistService backFilelistService;
    @Autowired
    private IDiskFileService diskFileService;
    @Autowired
    private IDiskShareFileService shareFileService;
    @Autowired
    private ISysUserService sysUserService;

    /**
     * 查询已上传文件列表列表
     */
    @PreAuthorize("@ss.hasPermi('disk:backFilelist:query')")
    @GetMapping("/list")
    public TableDataInfo list(DiskFile diskFile) {
        startPage("id desc");
//        List<BackFilelist> list = backFilelistService.selectBackFilelistList(backFilelist);
        List<DiskFile> list = diskFileService.selectDiskFileList(diskFile);

        List<Long> creators = list.stream().map(DiskFile::getCreateId).collect(Collectors.toList());
        List<Long> updaters = list.stream().map(DiskFile::getUpdateId).collect(Collectors.toList());

        List<Long> all = new ArrayList<>(creators);
        all.addAll(updaters);
        List<SysUser> sysUsers = sysUserService.selectUserByIds(all);

        Map<Long, SysUser> userMap = sysUsers.stream().collect(Collectors.toMap(SysUser::getUserId, sysUser -> sysUser));

        List<DiskShareFile> shareFiles = shareFileService.selectDiskShareFileList(new DiskShareFile());
        Map<Long, DiskShareFile> shareFileMap = shareFiles.stream().collect(Collectors.toMap(DiskShareFile::getId, shareFile -> shareFile));
        List<Long> shareFileIds = new ArrayList<>();
        shareFiles.forEach(shareFile -> {
            if (StringUtils.isEmpty(shareFile.getFileIds())) {
                return;
            }
            String[] array = StringUtils.split(shareFile.getFileIds(), ",");
            shareFileIds.addAll(Arrays.stream(array).map(Long::valueOf).collect(Collectors.toList()));
        });

        for (DiskFile file : list) {
            if (userMap.containsKey(file.getCreateId())) {
                file.setCreator(userMap.get(file.getCreateId()).getNickName());
            }
            if (userMap.containsKey(file.getUpdateId())) {
                file.setModified(userMap.get(file.getCreateId()).getNickName());
            }

            if(shareFileIds.contains(file.getId())){
                file.setShared(true);
            }
        }

        return getDataTable(list);
    }

    /**
     * 导出已上传文件列表列表
     */
    @PreAuthorize("@ss.hasPermi('disk:backFilelist:export')")
    @Log(title = "已上传文件列表", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(BackFilelist backFilelist) {
        List<BackFilelist> list = backFilelistService.selectBackFilelistList(backFilelist);
        ExcelUtil<BackFilelist> util = new ExcelUtil<BackFilelist>(BackFilelist.class);
        return util.exportExcel(list, "filelist");
    }

    /**
     * 获取已上传文件列表详细信息
     */
    @PreAuthorize("@ss.hasPermi('disk:backFilelist:detail')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return AjaxResult.success(backFilelistService.selectBackFilelistById(id));
    }

    /**
     * 新增已上传文件列表
     */
    @PreAuthorize("@ss.hasPermi('disk:backFilelist:add')")
    @Log(title = "已上传文件列表", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody BackFilelist backFilelist) {
        return toAjax(backFilelistService.insertBackFilelist(backFilelist));
    }

    /**
     * 修改已上传文件列表
     */
    @PreAuthorize("@ss.hasPermi('disk:backFilelist:edit')")
    @Log(title = "已上传文件列表", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody BackFilelist backFilelist) {
        return toAjax(backFilelistService.updateBackFilelist(backFilelist));
    }

    /**
     * 删除已上传文件列表
     */
    @PreAuthorize("@ss.hasPermi('disk:backFilelist:remove')")
    @Log(title = "已上传文件列表", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(diskFileService.deleteDiskFileByIds(ids));
    }
}
