package com.atsoptimizer.jobanalyzer.service;

import com.atsoptimizer.jobanalyzer.dto.JobResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class CacheService {

    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${cache.ttl}")
    private long cacheTtl;

    private static final String JOB_CACHE_PREFIX = "job:";
    private static final String USER_JOBS_CACHE_PREFIX = "user_jobs:";

    /**
     * Cache a job response
     */
    public void cacheJob(Long jobId, JobResponse jobResponse) {
        try {
            String key = JOB_CACHE_PREFIX + jobId;
            redisTemplate.opsForValue().set(key, jobResponse, cacheTtl, TimeUnit.SECONDS);
            log.debug("Cached job with id: {}", jobId);
        } catch (Exception e) {
            log.error("Error caching job: {}", jobId, e);
        }
    }

    /**
     * Get cached job
     */
    public JobResponse getCachedJob(Long jobId) {
        try {
            String key = JOB_CACHE_PREFIX + jobId;
            Object cached = redisTemplate.opsForValue().get(key);
            if (cached instanceof JobResponse) {
                log.debug("Cache hit for job: {}", jobId);
                return (JobResponse) cached;
            }
        } catch (Exception e) {
            log.error("Error retrieving cached job: {}", jobId, e);
        }
        return null;
    }

    /**
     * Invalidate job cache
     */
    public void invalidateJob(Long jobId) {
        try {
            String key = JOB_CACHE_PREFIX + jobId;
            redisTemplate.delete(key);
            log.debug("Invalidated cache for job: {}", jobId);
        } catch (Exception e) {
            log.error("Error invalidating cache for job: {}", jobId, e);
        }
    }

    /**
     * Invalidate user's jobs cache
     */
    public void invalidateUserJobs(String userId) {
        try {
            String key = USER_JOBS_CACHE_PREFIX + userId;
            redisTemplate.delete(key);
            log.debug("Invalidated cache for user jobs: {}", userId);
        } catch (Exception e) {
            log.error("Error invalidating user jobs cache: {}", userId, e);
        }
    }

    /**
     * Clear all cache
     */
    public void clearAllCache() {
        try {
            redisTemplate.getConnectionFactory().getConnection().flushAll();
            log.info("Cleared all cache");
        } catch (Exception e) {
            log.error("Error clearing all cache", e);
        }
    }
}