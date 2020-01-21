package com.example.gmall.search.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.example.gmall.bean.PmsSearchParam;
import com.example.gmall.bean.PmsSearchSkuInfo;
import com.example.gmall.service.SearchService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
public class SearchController {

    @Reference
    SearchService searchService;

    @RequestMapping("list")
    public String list(PmsSearchParam pmsSearchParam, ModelMap modelMap){
        // 调用搜索服务，返回搜索结果
        List<PmsSearchSkuInfo> pmsSearchSkuInfoList = searchService.list(pmsSearchParam);
        modelMap.put("skuLsInfoList", pmsSearchSkuInfoList);
        return "list";
    }

    @RequestMapping("index")
    public String index(){


        return "index";
    }
}
