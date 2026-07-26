package com.example.demo.service.userServices;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class PostViewRedisService {

    private final StringRedisTemplate stringRedisTemplate;

    // 照会数CheckおよびRedisの記録
    //　＠Return true：　最初に照会する（照会数増加対象）/　false：　もう照会する（カウントしない）
    public boolean checkAndSetViewHistory(Long postId, Long userId) {
        String key = "view:post:" + postId + ":user:" + userId;

        // setIfAbsent: Keyが存在してない時だけ保存
        //　1時間だけ維持
        Boolean isFirstView = stringRedisTemplate.opsForValue()
                .setIfAbsent(key, "1", Duration.ofHours(1));

        return Boolean.TRUE.equals(isFirstView);
    }

}
