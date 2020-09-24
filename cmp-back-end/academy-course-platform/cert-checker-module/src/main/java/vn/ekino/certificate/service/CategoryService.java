package vn.ekino.certificate.service;

import com.google.gson.Gson;
import vn.ekino.certificate.dto.CategoryDto;
import vn.ekino.certificate.model.data.Phase;
import vn.ekino.certificate.model.data.Program;
import vn.ekino.certificate.repository.CategoryRepository;
import vn.ekino.certificate.util.MapperUtils;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Inject
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<CategoryDto> findAllCourseCategories() {

        return categoryRepository
                .findAllPhaseCategories()
                .stream()
                .map(node -> MapperUtils.nodeToObject(node, CategoryDto.class).get())
                .collect(Collectors.toList());
    }

    public List<CategoryDto> findAllCategoriesOfCourse() {

        return categoryRepository
                .findAllCourseCategories()
                .stream()
                .map(node -> MapperUtils.nodeToObject(node, CategoryDto.class).get())
                .collect(Collectors.toList());
    }

    public Optional<CategoryDto> findCategoryById(String categoryId) {
        return MapperUtils.nodeToObject(categoryRepository.findById(categoryId).get(), CategoryDto.class);
    }

    public List<CategoryDto> findAllByPath(String path) {
        return categoryRepository
                .findAllByPath(path)
                .stream()
                .map(node -> MapperUtils.nodeToObject(node, CategoryDto.class).get())
                .collect(Collectors.toList());
    }

    public Map<String, Object> getPhaseAndGroup() {
        Map<String, Object> map = new HashMap<>();
        List<Phase> listPhase = new ArrayList<>();
        var list = findAllByPath("phases");
        for (int i = list.size() - 1; i > -1; i--) {
            listPhase.add(mapPhase(list.get(i)));
        }
        map.put("listPhase", listPhase);
        var listGroupProgram = findAllByPath("course-groups")
                .stream()
                .map(this::mapProgram)
                .collect(Collectors.toList());
        map.put("listGroupProgram", listGroupProgram);
        map.put("groupPrograms", new Gson().toJson(listGroupProgram));
        return map;
    }

    private Phase mapPhase(CategoryDto dto) {
        return Phase.builder()
                .id(dto.getUuid())
                .name(dto.getDisplayName())
                .title(dto.getDisplayName())
                .build();
    }

    private Program mapProgram(CategoryDto dto) {
        String className = "digital-project-manager";
        String displayName = dto.getDisplayName().toLowerCase();
        if (displayName.contains("foundation")) {
            className = "digital-foundation";
        } else if (displayName.contains("architect")) {
            className = "digital-architect";
        }
        return Program.builder()
                .id(dto.getUuid())
                .name(dto.getDisplayName())
                .className(className)
                .time("")
                .build();
    }


}
