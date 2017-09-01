package com.dataCollection.repo;

import com.dataCollection.entity.JobStatus;
import com.dataCollection.entity.PipelineJob;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by xiangrchen on 8/9/17.
 */
@Repository
public interface PipelineJobRepo extends CrudRepository<PipelineJob,Long>{

    List<PipelineJob> findByStatus(JobStatus.Status state);

    List<PipelineJob> findByStatusIn(List<JobStatus.Status> stateList);

//    @Transactional
//    @Modifying
//    @Query("update PipelineJob p "+
//            "set p.state= ?2 where p.id= ?1")
//    void update(Long Id, JobState.State state);

}
