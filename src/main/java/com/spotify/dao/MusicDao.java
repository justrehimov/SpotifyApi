package com.spotify.dao;

import com.spotify.dto.response.MusicResponse;
import com.spotify.entity.Music;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class MusicDao {

    @PersistenceContext
    private final EntityManager entityManager;

    public List<Music> list(String filter){
        Session session = entityManager.unwrap(Session.class);
        String sql = "select m from Music m";
        String where = "";
        Map<String, Object> values = new HashMap<>();
        if(StringUtils.hasText(filter)){
            where += "and lower(m.name) like lower(:filter) ";
            values.put("filter", filter);
        }
        if(StringUtils.hasText(where)){
            sql+= " where " + where.substring(3);
        }
        Query query = session.createQuery(sql);
        if(!values.isEmpty()){
            values.entrySet().stream().forEach(entry -> {
                query.setParameter(entry.getKey(), entry.getValue());
            });
        }
        List<Music> musicList = query.getResultList();
        return musicList;
    }
}
