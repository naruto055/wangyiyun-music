package com.naruto.wangyiyunmusic.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.naruto.wangyiyunmusic.model.dto.BilibiliSearchDTO;
import com.naruto.wangyiyunmusic.model.vo.BilibiliCookieVO;
import com.naruto.wangyiyunmusic.model.vo.BilibiliVideoVO;
import com.naruto.wangyiyunmusic.service.BilibiliSearchService;
import com.naruto.wangyiyunmusic.service.SearchHistoryService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * B站视频搜索服务实现类
 *
 * <p>基于HTTP直接调用B站官方API实现视频搜索功能</p>
 *
 * @Author: naruto
 * @CreateTime: 2026-02-05
 */
@Service
@Slf4j
public class BilibiliSearchServiceImpl implements BilibiliSearchService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private SearchHistoryService searchHistoryService;

    @Value("${bilibili.search.api-url}")
    private String searchApiUrl;

    @Value("${bilibili.search.cookie-url}")
    private String cookieApiUrl;

    @Value("${bilibili.search.user-agent}")
    private String userAgent;

    @Value("${bilibili.search.default-page-size}")
    private Integer defaultPageSize;

    /**
     * 缓存的Cookie（内存缓存，有效期1天）
     */
    private BilibiliCookieVO cachedCookie;

    /**
     * 获取B站Cookie（buvid3和buvid4）
     *
     * @return Cookie信息
     */
    @Override
    public BilibiliCookieVO getBilibiliCookie() {
        // 检查缓存是否有效
        if (cachedCookie != null && cachedCookie.getExpireTime().isAfter(LocalDateTime.now())) {
            log.debug("使用缓存的B站Cookie");
            return cachedCookie;
        }

        log.info("从B站API获取新Cookie");

        try {
            // 设置请求头 - 关键:使用iPhone移动端User-Agent获取Cookie
            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 13_2_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.0.3 Mobile/15E148 Safari/604.1 Edg/114.0.0.0");

            HttpEntity<String> entity = new HttpEntity<>(headers);

            // 调用Cookie API
            ResponseEntity<Map> response = restTemplate.exchange(
                    cookieApiUrl,
                    HttpMethod.GET,
                    entity,
                    Map.class
            );

            // 解析响应
            Map<String, Object> body = response.getBody();
            if (body == null) {
                throw new RuntimeException("B站Cookie API返回数据为空");
            }

            Integer code = (Integer) body.get("code");
            if (code == null || code != 0) {
                throw new RuntimeException("B站Cookie API返回错误: " + body.get("message"));
            }

            Map<String, String> data = (Map<String, String>) body.get("data");
            if (data == null) {
                throw new RuntimeException("B站Cookie API返回数据格式错误");
            }

            // 构建Cookie VO
            BilibiliCookieVO cookie = new BilibiliCookieVO();
            cookie.setB3(data.get("b_3"));
            cookie.setB4(data.get("b_4"));
            cookie.setExpireTime(LocalDateTime.now().plusDays(1)); // 缓存1天

            // 更新缓存
            cachedCookie = cookie;

            log.info("成功获取B站Cookie, buvid3={}, buvid4={}", cookie.getB3(), cookie.getB4());

            return cookie;
        } catch (Exception e) {
            log.error("获取B站Cookie失败", e);
            throw new RuntimeException("获取B站Cookie失败: " + e.getMessage(), e);
        }
    }

    /**
     * 搜索B站视频
     *
     * @param searchDTO 搜索请求参数
     * @return 分页搜索结果
     */
    @Override
    public IPage<BilibiliVideoVO> searchVideos(BilibiliSearchDTO searchDTO) {
        log.info("搜索B站视频, keyword={}, page={}, pageSize={}",
                searchDTO.getKeyword(), searchDTO.getPage(), searchDTO.getPageSize());

        try {
            // 1. 获取Cookie
            BilibiliCookieVO cookie = getBilibiliCookie();

            // 2. 构建请求URI（关键：使用 URI 对象避免 RestTemplate 二次编码）
            java.net.URI requestUri = UriComponentsBuilder.fromHttpUrl(searchApiUrl)
                    .queryParam("context", "")
                    .queryParam("page", searchDTO.getPage())
                    .queryParam("order", "")
                    .queryParam("page_size", searchDTO.getPageSize())
                    .queryParam("keyword", searchDTO.getKeyword())
                    .queryParam("duration", "")
                    .queryParam("tids_1", "")
                    .queryParam("tids_2", "")
                    .queryParam("__refresh__", true)
                    .queryParam("_extra", "")
                    .queryParam("highlight", 1)
                    .queryParam("single_column", 0)
                    .queryParam("platform", "pc")
                    .queryParam("from_source", "")
                    .queryParam("search_type", "video")
                    .queryParam("dynamic_offset", 0)
                    .build()
                    .encode()  // 正确编码URL
                    .toUri();  // 关键：返回 URI 对象，避免 RestTemplate 二次编码

            // 3. 设置请求头（参照extion.js的searchHeaders配置）
            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", userAgent);
            headers.set("Accept", "application/json, text/plain, */*");
            // 注意：不设置 Accept-Encoding，避免 gzip 解压问题
            headers.set("Origin", "https://search.bilibili.com");
            headers.set("Sec-Fetch-Site", "same-site");
            headers.set("Sec-Fetch-Mode", "cors");
            headers.set("Sec-Fetch-Dest", "empty");
            headers.set("Referer", "https://search.bilibili.com/");
            headers.set("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8,en-GB;q=0.7,en-US;q=0.6");
            headers.set("Cookie", "buvid3=" + cookie.getB3() + ";buvid4=" + cookie.getB4());

            HttpEntity<String> entity = new HttpEntity<>(headers);

            // 打印完整的请求URL（用于调试）
            log.info("===== B站搜索请求URL =====");
            log.info("URL: {}", requestUri);

            // 4. 发送请求（使用 URI 对象而不是 String）
            ResponseEntity<Map> response = restTemplate.exchange(
                    requestUri,  // 传递 URI 对象
                    HttpMethod.GET,
                    entity,
                    Map.class
            );

            // 打印完整的响应（用于调试）
            log.info("===== B站搜索响应 =====");
            log.info("Response Body: {}", response.getBody());

            // 5. 解析响应
            Map<String, Object> body = response.getBody();
            if (body == null) {
                throw new RuntimeException("B站搜索API返回数据为空");
            }

            Integer code = (Integer) body.get("code");
            if (code == null || code != 0) {
                throw new RuntimeException("B站搜索API返回错误: " + body.get("message"));
            }

            Map<String, Object> data = (Map<String, Object>) body.get("data");
            if (data == null) {
                log.warn("搜索结果为空, keyword={}", searchDTO.getKeyword());
                // 返回空结果
                Page<BilibiliVideoVO> page = new Page<>(searchDTO.getPage(), searchDTO.getPageSize());
                page.setTotal(0);
                return page;
            }

            List<Map<String, Object>> results = (List<Map<String, Object>>) data.get("result");
            if (results == null) {
                results = List.of();
            }

            // 6. 转换为VO
            List<BilibiliVideoVO> voList = results.stream()
                    .map(this::convertToVO)
                    .collect(Collectors.toList());

            // 7. 构建分页对象
            Page<BilibiliVideoVO> page = new Page<>(searchDTO.getPage(), searchDTO.getPageSize());
            page.setRecords(voList);

            // 获取总数
            Object numResults = data.get("numResults");
            if (numResults instanceof Integer) {
                page.setTotal((Integer) numResults);
            } else if (numResults instanceof Long) {
                page.setTotal((Long) numResults);
            } else {
                page.setTotal(voList.size());
            }

            // 8. 记录搜索历史（异步记录，不影响搜索结果返回）
            try {
                searchHistoryService.recordSearch(null, searchDTO.getKeyword());
            } catch (Exception e) {
                log.error("记录搜索历史失败, keyword={}", searchDTO.getKeyword(), e);
                // 记录失败不影响搜索功能，仅记录日志
            }

            return page;

        } catch (Exception e) {
            log.error("搜索B站视频失败, keyword={}", searchDTO.getKeyword(), e);
            throw new RuntimeException("搜索B站视频失败: " + e.getMessage(), e);
        }
    }

    /**
     * 转换为VO（简化版 v1.3）
     *
     * @param result B站API返回的单个视频数据
     * @return BilibiliVideoVO
     */
    private BilibiliVideoVO convertToVO(Map<String, Object> result) {
        BilibiliVideoVO vo = new BilibiliVideoVO();

        try {
            // 1. 提取并清理标题（移除<em>标签，HTML实体解码）
            String title = (String) result.get("title");
            if (title != null) {
                title = title.replaceAll("<em class=\"keyword\">", "")
                        .replaceAll("</em>", "");
                title = StringEscapeUtils.unescapeHtml4(title);
                vo.setTitle(title);
            }

            // 2. 设置 bvid
            String bvid = (String) result.get("bvid");
            vo.setBvid(bvid);

            // 3. 设置时长（格式：MM:SS 或 HH:MM:SS）
            String duration = (String) result.get("duration");
            vo.setDuration(duration);

            // 4. 构建视频链接
            if (bvid != null) {
                vo.setUrl("https://www.bilibili.com/video/" + bvid);
            }

        } catch (Exception e) {
            log.error("转换视频数据失败", e);
        }

        return vo;
    }
}
