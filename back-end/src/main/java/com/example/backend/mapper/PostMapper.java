package com.example.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.backend.entity.dto.Interact;
import com.example.backend.entity.dto.Post;
import com.example.backend.entity.vo.respones.PostDetailVO;
import com.example.backend.entity.vo.respones.TopPostVO;
import com.example.backend.utils.SqlProvider;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@SuppressWarnings("SqlNoDataSourceInspection")
@Mapper
public interface PostMapper extends BaseMapper<Post> {

    /*
     @Select("""
           select * from db_post left join db_account on db_post.uid = db_account.id
           order by `create_time` desc limit ${start},10
           """)
    List<Post> postList(int start);

    @Select("""
           select * from db_post left join db_account on db_post.uid = db_account.id
           where db_post.post_type = ${type}
           order by `create_time` desc limit ${start},10
           """)
    List<Post> postListByType(int start,int type);
     */

    @Select("""
            select * from db_post where `top` = 1
            """)
    List<TopPostVO> getTopPost();

    @Insert("""
            <script>
                insert ignore into db_post_interact_${type} values 
                <foreach collection="interacts" item="item" separator=",">
                    (#{item.pid},#{item.uid},#{item.time})
                </foreach>
            </script>
            """)
    void addInsert(List<Interact> interacts, String type);

    @Insert("""
            <script>
                delete from db_post_interact_${type} where 
                <foreach collection="interacts" item="item" separator=" or ">
                    (pid = #{item.pid} and uid = #{item.uid})
                </foreach>
            </script>
            """)
    void deleteInsert(List<Interact> interacts, String type);

    @Select("""
            select count(*) from db_post_interact_${type} where pid = #{pid}
            """)
    int interactCount(int pid, String type);

    @Select("""
            select count(*) from db_post_interact_${type} where pid = #{pid} and uid = #{uid}
            """)
    int userInteractCount(int pid, int uid, String type);
    @Select("""
            select title,post_type,id from db_post_interact_collect 
            left join db_post on db_post_interact_collect.pid = db_post.id
            where db_post_interact_collect.uid = #{userId}
            """)
    List<PostDetailVO> listPostCollects(int userId);

    @Delete("""
            
            delete from db_post_interact_collect 
            where uid = #{userId} and pid = #{pid}
            """)
    int removeCollect(int userId, int pid);

    @UpdateProvider(type = SqlProvider.class, method = "updateViews")
    Long updateViews(Map<Integer, Long> map);

}
