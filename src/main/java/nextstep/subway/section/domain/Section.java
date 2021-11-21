package nextstep.subway.section.domain;

import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.util.Objects;

@Entity
public class Section {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "line_id")
    private Line line;

    @ManyToOne
    @JoinColumn(name = "station_id")
    private Station station;

    private int distance;

    public Section() {
    }

    public Section(Line line, Station station, int distance) {
        changeLine(line);
        changeStation(station);
        this.distance = distance;
    }

    public static Section of(Line line, Station station, int distance) {
        return new Section(line, station, distance);
    }

    public void changeLine(Line line) {
        if (!Objects.isNull(this.line)) {
            this.line.getSections().remove(this);
        }

        addLine(line);
    }

    public void addLine(Line line) {
        if (!Objects.isNull(line)) {
            line.getSections().add(this);
        }

        this.line = line;
    }

    public void changeStation(Station station) {
        if (!Objects.isNull(this.station)) {
            this.station.getSections().remove(this);
        }

        addStation(station);
    }

    public void addStation(Station station) {
        if (!Objects.isNull(station)) {
            station.getSections().add(this);
        }

        this.station = station;
    }

    public Long getId() {
        return id;
    }

    public Line getLine() {
        return line;
    }

    public Station getStation() {
        return station;
    }

    public int getDistance() {
        return distance;
    }
}
