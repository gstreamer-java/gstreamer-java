/* 
 * Copyright (c) 2007 Wayne Meissner
 * 
 * This file is part of gstreamer-java.
 *
 * gstreamer-java is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * gstreamer-java is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with gstreamer-java.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.gstreamer;

/**
 *
 */
public enum Tag {
    ARTIST("artist"),
    TITLE("title"),
    ALBUM("album"),
    GENRE("genre"),
    COMMENT("comment"),
    EXTENDED_COMMENT("extended-comment"),
    LOCATION("location"),
    DESCRIPTION("description"),
    VERSION("version"),
    ORGANIZATION("organization"),
    COPYRIGHT("copyright"),
    CONTACT("contact"),
    LICENSE("license"),
    PERFORMER("performer"),
    CODEC("codec"),
    AUDIO_CODEC("audio-codec"),
    VIDEO_CODEC("video-codec"),
    ENCODER("encoder"),
    ENCODER_VERSION("encoder-version"),
    LANGUAGE_CODE("language-code"),
    TRACK_NUMBER("track-number"),
    TRACK_COUNT("track-count"),
    ALBUM_VOLUME_NUMBER("album-disc-number"),
    ALBUM_VOLUME_COUNT("album-disc-count"),
    BITRATE("bitrate"),
    NOMINAL_BITRATE("nominal-bitrate"),
    MINIMUM_BITRATE("minimum-bitrate"),
    MAXIMUM_BITRATE("maximum-bitrate"),
    TRACK_GAIN("replaygain-track-gain"),
    TRACK_PEAK("replaygain-track-peak"),
    ALBUM_GAIN("replaygain-album-gain"),
    ALBUM_PEAK("replaygain-album-peak"),
    REFERENCE_LEVEL("replaygain-reference-level"),
    SERIAL("serial"),
    DATE("date"),
    DURATION("duration"),
    ISRC("isrc"),
    IMAGE("image"),
    PREVIEW_IMAGE("preview-image"),
    BEATS_PER_MINUTE("beats-per-minute");
    
    Tag(String id) {
        this.id = id;
    }
     public String getId() {
        return id;
    }    
    private String id;

   
}
