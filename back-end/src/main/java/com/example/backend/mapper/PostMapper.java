package com.example.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.backend.entity.dto.Interact;
import com.example.backend.entity.dto.Post;
import com.example.backend.entity.vo.respones.TopPostVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

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
    void addInsert(List<Interact> interacts,String type);

    @Insert("""
            <script>
                delete from db_post_interact_${type} where 
                <foreach collection="interacts" item="item" separator=" or ">
                    (pid = #{item.pid} and uid = #{item.uid})
                </foreach>
            </script>
            """)
    void deleteInsert(List<Interact> interacts,String type);
}
