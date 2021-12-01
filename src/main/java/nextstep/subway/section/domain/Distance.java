package nextstep.subway.section.domain;

import nextstep.subway.common.exception.NegativeNumberDistanceException;

import javax.persistence.Embeddable;
import java.util.Objects;

@Embeddable
public class Distance {
    public static final int MINIMUM_DISTANCE = 1;

    private int distance;

    protected Distance() {
    }

    public Distance(int distance) {
        validateDistance(distance);
        this.distance = distance;
    }

    private void validateDistance(int distance) {
        if (distance < MINIMUM_DISTANCE) {
            throw new NegativeNumberDistanceException(distance);
        }
    }

    public int getDistance() {
        return distance;
    }

    public void changeDistance(int distance) {
        this.distance = distance;
    }

    public void subtractDistance(int distance) {
        validateDistance(this.distance - distance);
        this.distance -= distance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Distance distance1 = (Distance) o;
        return distance == distance1.distance;
    }

    @Override
    public int hashCode() {
        return Objects.hash(distance);
    }
}
