package nextstep.subway.line.domain;

import nextstep.subway.line.domain.wrappers.LineStations;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Line entity 테스트")
class LineTest {

    @Test
    @DisplayName("아이디 기준 노선 조회 결과가 없을 시 에러 정상 발생")
    void checkNullLine() {
        Optional<Line> emptyLine = Optional.empty();
        assertThatThrownBy(() -> Line.getNotNullLine(emptyLine)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("노선 데이터 변경")
    void update() {
        Line expected = new Line("분당선", "bg-red-600");
        Line updateLine = new Line("구분당선", "bg-red-600");
        expected.update(updateLine);
        assertThat(expected.getId()).isEqualTo(updateLine.getId());
        assertThat(expected.getName()).isEqualTo(updateLine.getName());
        assertThat(expected.getColor()).isEqualTo(updateLine.getColor());
    }

    @Test
    void 노선_정보에_노선_지하철역_연결_테이블_정보_추가() {
        Station station = new Station(2L, "정자역");
        Station preStation = new Station(1L, "양재역");
        Line line = new Line("신분당선", "bg - red - 600");
        LineStation lineStation = new LineStation(station, preStation, 10);
        line.addLineStation(lineStation);
        Line expected = new Line("신분당선", "bg - red - 600").lineStationsBy(new LineStations(Arrays.asList(lineStation)));
        assertThat(line.stations()).isEqualTo(expected.stations());
    }

    @Test
    @DisplayName("노선 정보의 LineStations에 동일한 LineStations가 존재할 경우 에러 발생")
    void checkValidDuplicateLineStation() {
        Station station = new Station(2L, "정자역");
        Station preStation = new Station(1L, "양재역");
        Line line = new Line("신분당선", "bg - red - 600");
        LineStation lineStation = new LineStation(station, preStation, 10);
        line.addLineStation(lineStation);
        assertThatThrownBy(() -> line.checkValidLineStation(lineStation))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("상행역 양재역 하행역 정자역은 이미 등록된 구간 입니다.");
    }

    @Test
    @DisplayName("노선 정보의 LineStations에 상행역, 하행역 둘중 하나라도 포함한 lineStation이 존재하지않을때 에러 발생")
    void checkValidNotContainStations() {
        Station station = new Station(2L, "정자역");
        Station preStation = new Station(1L, "양재역");
        Line line = new Line("신분당선", "bg - red - 600");
        LineStation actual = new LineStation(station, preStation, 10);
        line.addLineStation(actual);
        LineStation lineStation = new LineStation(new Station(7L, "매봉역"), new Station(8L, "교대역"), 10);
        assertThatThrownBy(() -> line.checkValidLineStation(lineStation))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("상행역 교대역, 하행역 매봉역을 둘중 하나라도 포함하는 구간이 존재하지않습니다.");
    }

    @Test
    @DisplayName("노선에 속한 구간이 하나만 존재할 시 에러 발생")
    void checkValidSingleSection() {
        Station station = new Station(2L, "정자역");
        Station preStation = new Station(1L, "양재역");
        Line line = new Line("신분당선", "bg - red - 600");
        LineStation lineStation1 = new LineStation(station, preStation, 10);
        LineStation lineStation2 = new LineStation(preStation, null, 0);
        line.addLineStation(lineStation1);
        line.addLineStation(lineStation2);
        assertThatThrownBy(() -> line.checkValidSingleSection())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("구간이 하나만 존재하는 경우 구간을 삭제할 수 없습니다.");
    }

    @Test
    void 지하철역_기준으로_lineStation_찾기() {
        Station station = new Station(2L, "정자역");
        Station preStation = new Station(1L, "양재역");
        Line line = new Line("신분당선", "bg - red - 600");
        LineStation lineStation1 = new LineStation(station, preStation, 10);
        LineStation lineStation2 = new LineStation(preStation, null, 0);
        line.addLineStation(lineStation1);
        line.addLineStation(lineStation2);

        assertThat(line.findLineStationByStation(station)).isEqualTo(lineStation1);
        assertThat(line.findLineStationByStation(preStation)).isEqualTo(lineStation2);
    }
}