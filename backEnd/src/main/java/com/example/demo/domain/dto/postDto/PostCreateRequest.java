package com.example.demo.domain.dto.postDto;

import com.example.demo.domain.entity.post.MediaType;
import com.example.demo.domain.entity.post.Visibility;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PostCreateRequest {

    private String content;
    private boolean isStory;
    private Visibility visibility = Visibility.PUBLIC;
    private List<MediaInfo> medias;

    @Getter
    @Setter
    public static class MediaInfo {
        private String mediaUrl;
        private String thumbnailUrl;
        private MediaType mediaType;
        private Integer sortOrder;
    }
}
