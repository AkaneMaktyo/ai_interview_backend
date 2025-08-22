package com.zsj.aiinterview.controller;

import com.zsj.aiinterview.entity.KnowledgeTag;
import com.zsj.aiinterview.service.KnowledgeTagService;
import com.zsj.aiinterview.util.ResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 知识点标签控制器
 */
@RestController
@RequestMapping("/api/tags")

@RequiredArgsConstructor
public class KnowledgeTagController {

    private final KnowledgeTagService knowledgeTagService;

    @GetMapping("/list")
    public Map<String, Object> getAllTags() {
        try {
            List<KnowledgeTag> tags = knowledgeTagService.findAll();
            return ResponseUtil.successWithCount(tags, tags.size());
        } catch (Exception e) {
            return ResponseUtil.error(e.getMessage());
        }
    }

    @GetMapping("/get")
    public Map<String, Object> getTagById(@RequestParam Long id) {
        try {
            Optional<KnowledgeTag> tag = knowledgeTagService.findById(id);
            return tag.isPresent() ? ResponseUtil.success(tag.get()) : ResponseUtil.error("标签不存在");
        } catch (Exception e) {
            return ResponseUtil.error(e.getMessage());
        }
    }

    @GetMapping("/by-category")
    public Map<String, Object> getTagsByCategory(@RequestParam String category) {
        try {
            List<KnowledgeTag> tags = knowledgeTagService.findByCategory(category);
            Map<String, Object> response = ResponseUtil.successWithCount(tags, tags.size());
            response.put("category", category);
            return response;
        } catch (Exception e) {
            return ResponseUtil.error(e.getMessage());
        }
    }

    @GetMapping("/categories")
    public Map<String, Object> getAllCategories() {
        try {
            List<String> categories = knowledgeTagService.getAllCategories();
            return ResponseUtil.successWithCount(categories, categories.size());
        } catch (Exception e) {
            return ResponseUtil.error(e.getMessage());
        }
    }

    @PostMapping("/create")
    public Map<String, Object> createTag(@RequestParam String name,
                                       @RequestParam(required = false) String description,
                                       @RequestParam(required = false) String category) {
        try {
            if (knowledgeTagService.findByName(name).isPresent()) {
                return ResponseUtil.error("标签名称已存在");
            }

            KnowledgeTag tag = new KnowledgeTag();
            tag.setName(name);
            tag.setDescription(description);
            tag.setCategory(category != null ? category : "technical");
            
            KnowledgeTag savedTag = knowledgeTagService.save(tag);
            return ResponseUtil.success(savedTag, "标签创建成功");
        } catch (Exception e) {
            return ResponseUtil.error(e.getMessage());
        }
    }

    @PostMapping("/update")
    public Map<String, Object> updateTag(@RequestParam Long id,
                                       @RequestParam(required = false) String name,
                                       @RequestParam(required = false) String description,
                                       @RequestParam(required = false) String category) {
        try {
            Optional<KnowledgeTag> existingTag = knowledgeTagService.findById(id);
            if (!existingTag.isPresent()) {
                return ResponseUtil.error("标签不存在");
            }
            
            KnowledgeTag tag = existingTag.get();
            if (name != null) tag.setName(name);
            if (description != null) tag.setDescription(description);
            if (category != null) tag.setCategory(category);
            
            KnowledgeTag updatedTag = knowledgeTagService.update(id, tag);
            return ResponseUtil.success(updatedTag, "标签更新成功");
        } catch (Exception e) {
            return ResponseUtil.error(e.getMessage());
        }
    }

    @PostMapping("/delete")
    public Map<String, Object> deleteTag(@RequestParam Long id) {
        try {
            boolean deleted = knowledgeTagService.deleteById(id);
            return deleted ? ResponseUtil.success(null, "标签删除成功") : ResponseUtil.error("标签不存在或删除失败");
        } catch (Exception e) {
            return ResponseUtil.error(e.getMessage());
        }
    }

    @GetMapping("/count")
    public Map<String, Object> getTagCount() {
        try {
            long count = knowledgeTagService.count();
            return ResponseUtil.count(count);
        } catch (Exception e) {
            return ResponseUtil.error(e.getMessage());
        }
    }
}