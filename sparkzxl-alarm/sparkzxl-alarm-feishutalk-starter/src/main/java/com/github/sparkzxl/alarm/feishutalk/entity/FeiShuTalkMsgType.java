/*
 * Copyright ©2015-2022 Jaemon. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.sparkzxl.alarm.feishutalk.entity;

/**
 * 飞书支持的消息类型
 *
 * @author Jaemon
 * @since 1.0
 */
public enum FeiShuTalkMsgType {
    /**
     * text类型
     */
    TEXT("text"),

    /**
     * link类型
     */
    LINK("link"),

    /**
     * markdown类型
     */
    MARKDOWN("markdown"),

    /**
     * ActionCard类型
     */
    ACTION_CARD("interactive"),

    /**
     * FeedCard类型
     */
    FEED_CARD("feedCard");

    private final String type;

    FeiShuTalkMsgType(String type) {
        this.type = type;
    }

    public String type() {
        return type;
    }
}