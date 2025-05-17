package com.techbytedev.signboardmanager.service;
import com.techbytedev.signboardmanager.entity.Permission;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class PermissionSpecification {

    public Specification<Permission> buildSpecification(String name, String apiPath, String method, String module) {
        Specification<Permission> spec = Specification.where(null);

        // Điều kiện lọc theo tên (name)
        if (name != null && !name.isEmpty()) {
            if (name.startsWith("LIKE:")) {
                String pattern = "%" + name.replace("LIKE:", "") + "%";
                spec = spec.and((root, query, cb) -> cb.like(root.get("name"), pattern));
            } else {
                spec = spec.and((root, query, cb) -> cb.equal(root.get("name"), name));
            }
        }

        // Điều kiện lọc theo đường dẫn API (apiPath)
        if (apiPath != null && !apiPath.isEmpty()) {
            if (apiPath.startsWith("LIKE:")) {
                String pattern = "%" + apiPath.replace("LIKE:", "") + "%";
                spec = spec.and((root, query, cb) -> cb.like(root.get("apiPath"), pattern));
            } else {
                spec = spec.and((root, query, cb) -> cb.equal(root.get("apiPath"), apiPath));
            }
        }

        // Điều kiện lọc theo phương thức (method)
        if (method != null && !method.isEmpty()) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("method"), method));
        }

        // Điều kiện lọc theo module
        if (module != null && !module.isEmpty()) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("module"), module));
        }

        return spec;
    }
}