package vn.iotstar.dao.impl;

import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import vn.iotstar.config.JPAConfig;
import vn.iotstar.dao.ICategoryDao;
import vn.iotstar.entity.Category;

/**
 * Lớp triển khai ICategoryDao sử dụng JPA 3.0 / Hibernate EntityManager
 */
public class CategoryDao implements ICategoryDao {

    @Override
    public void insert(Category category) {
        EntityManager enma = JPAConfig.getEntityManager();
        EntityTransaction trans = enma.getTransaction();
        try {
            trans.begin();
            enma.persist(category); // Insert entity vào bảng
            trans.commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (trans.isActive()) {
                trans.rollback();
            }
            throw e;
        } finally {
            enma.close();
        }
    }

    @Override
    public void update(Category category) {
        EntityManager enma = JPAConfig.getEntityManager();
        EntityTransaction trans = enma.getTransaction();
        try {
            trans.begin();
            enma.merge(category); // Update entity vào bảng
            trans.commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (trans.isActive()) {
                trans.rollback();
            }
            throw e;
        } finally {
            enma.close();
        }
    }

    @Override
    public void delete(int cateid) throws Exception {
        EntityManager enma = JPAConfig.getEntityManager();
        EntityTransaction trans = enma.getTransaction();
        try {
            trans.begin();
            Category category = enma.find(Category.class, cateid);
            if (category != null) {
                enma.remove(category); // Xóa entity khỏi database
            } else {
                throw new Exception("Không tìm thấy Category với mã ID = " + cateid);
            }
            trans.commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (trans.isActive()) {
                trans.rollback();
            }
            throw e;
        } finally {
            enma.close();
        }
    }

    @Override
    public Category findById(int cateid) {
        EntityManager enma = JPAConfig.getEntityManager();
        try {
            return enma.find(Category.class, cateid);
        } finally {
            enma.close();
        }
    }

    @Override
    public Category findByCategoryname(String name) throws Exception {
        EntityManager enma = JPAConfig.getEntityManager();
        String jpql = "SELECT c FROM Category c WHERE c.categoryname = :catename";
        try {
            TypedQuery<Category> query = enma.createQuery(jpql, Category.class);
            query.setParameter("catename", name);
            List<Category> list = query.getResultList();
            if (list == null || list.isEmpty()) {
                return null;
            }
            return list.get(0);
        } finally {
            enma.close();
        }
    }

    @Override
    public List<Category> findAll() {
        EntityManager enma = JPAConfig.getEntityManager();
        try {
            TypedQuery<Category> query = enma.createNamedQuery("Category.findAll", Category.class);
            return query.getResultList();
        } finally {
            enma.close();
        }
    }

    @Override
    public List<Category> searchByName(String catname) {
        EntityManager enma = JPAConfig.getEntityManager();
        // Sửa lỗi sai tên trường và tên tham số trong tài liệu mẫu
        String jpql = "SELECT c FROM Category c WHERE c.categoryname LIKE :keyword";
        try {
            TypedQuery<Category> query = enma.createQuery(jpql, Category.class);
            query.setParameter("keyword", "%" + catname + "%");
            return query.getResultList();
        } finally {
            enma.close();
        }
    }

    @Override
    public List<Category> findAll(int page, int pagesize) {
        EntityManager enma = JPAConfig.getEntityManager();
        try {
            TypedQuery<Category> query = enma.createNamedQuery("Category.findAll", Category.class);
            query.setFirstResult(page * pagesize);
            query.setMaxResults(pagesize);
            return query.getResultList();
        } finally {
            enma.close();
        }
    }

    @Override
    public int count() {
        EntityManager enma = JPAConfig.getEntityManager();
        String jpql = "SELECT COUNT(c) FROM Category c";
        try {
            Query query = enma.createQuery(jpql);
            Long count = (Long) query.getSingleResult();
            return count != null ? count.intValue() : 0;
        } finally {
            enma.close();
        }
    }
}
