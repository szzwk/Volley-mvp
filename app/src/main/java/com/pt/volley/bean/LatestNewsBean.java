package com.pt.volley.bean;

import java.util.List;

/**
 * @author: yorkzhang
 * @time: 16/7/21 17:59
 * @email: xtcqw13@126.com
 * @note:
 */
public class LatestNewsBean {

    private String date;
    private List<Story> stories;
    private List<TopStory> top_stories;

    public class Story {
        private List<String> images;
        private int type;
        private int id;
        private String ga_prefix;
        private String title;

        public List<String> getImages() {
            return images;
        }

        public int getType() {
            return type;
        }

        public int getId() {
            return id;
        }

        public String getGa_prefix() {
            return ga_prefix;
        }

        public String getTitle() {
            return title;
        }
    }

    public class TopStory {
        private String image;
        private int type;
        private int id;
        private String ga_prefix;
        private String title;

        public String getImage() {
            return image;
        }

        public int getType() {
            return type;
        }

        public int getId() {
            return id;
        }

        public String getGa_prefix() {
            return ga_prefix;
        }

        public String getTitle() {
            return title;
        }
    }

    public String getDate() {
        return date;
    }

    public List<Story> getStories() {
        return stories;
    }

    public List<TopStory> getTop_stories() {
        return top_stories;
    }
}
