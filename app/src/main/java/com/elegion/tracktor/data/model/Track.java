package com.elegion.tracktor.data.model;

import java.util.Date;
import java.util.Objects;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * @author Azret Magometov
 */
public class Track extends RealmObject {

    @PrimaryKey
    private long id;

    private Date date;

    private long duration;

    private Double distance;

    private String imageBase64;

    private float averageSpeed;

    private float calories;

    private int actionType;

    private String comment = "Введите комментарий";

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setAverageSpeed(float averageSpeed) {
        this.averageSpeed = averageSpeed;
    }

    public void setCalories(float calories) {
        this.calories = calories;
    }

    public void setActionType(int actionType) {
        this.actionType = actionType;
    }

    public float getAverageSpeed() {
        return averageSpeed;
    }

    public float getCalories() {
        return calories;
    }

    public int getActionType() {
        return actionType;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public void setImageBase64(String imageBase64) {
        this.imageBase64 = imageBase64;
    }

    public String getImageBase64() {
        return imageBase64;
    }

    @Override
    public String toString() {
        return "Track{" +
                "id=" + id +
                ", date=" + date +
                ", duration=" + duration +
                ", distance=" + distance +
                ", imageBase64=" + imageBase64 +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Track track = (Track) o;

        if (id != track.id) return false;
        if (duration != track.duration) return false;
        if (Float.compare(track.averageSpeed, averageSpeed) != 0) return false;
        if (Float.compare(track.calories, calories) != 0) return false;
        if (actionType != track.actionType) return false;
        if (date != null ? !date.equals(track.date) : track.date != null) return false;
        if (distance != null ? !distance.equals(track.distance) : track.distance != null)
            return false;
        if (imageBase64 != null ? !imageBase64.equals(track.imageBase64) : track.imageBase64 != null)
            return false;
        return comment != null ? comment.equals(track.comment) : track.comment == null;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (date != null ? date.hashCode() : 0);
        result = 31 * result + (int) (duration ^ (duration >>> 32));
        result = 31 * result + (distance != null ? distance.hashCode() : 0);
        result = 31 * result + (imageBase64 != null ? imageBase64.hashCode() : 0);
        result = 31 * result + (averageSpeed != +0.0f ? Float.floatToIntBits(averageSpeed) : 0);
        result = 31 * result + (calories != +0.0f ? Float.floatToIntBits(calories) : 0);
        result = 31 * result + actionType;
        result = 31 * result + (comment != null ? comment.hashCode() : 0);
        return result;
    }
}


