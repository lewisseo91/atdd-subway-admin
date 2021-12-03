package nextstep.subway.section;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.LineAcceptanceTest;
import nextstep.subway.line.LineAcceptanceFixture;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.StationAcceptanceTest;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("구간 관련 기능")
public class SectionAcceptanceTest extends AcceptanceTest {
    private LineResponse lineResponse;
    private StationResponse 강남역;
    private StationResponse 역삼역;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();

        강남역 = StationAcceptanceTest.지하철역을_생성한다("강남역");
        역삼역 = StationAcceptanceTest.지하철역을_생성한다("역삼역");
        lineResponse = LineAcceptanceTest.노선을_생성한다(강남역, 역삼역, 9, "2호선", "green");

    }

    @DisplayName("새로운 역을 상행 종점으로 등록한다.")
    @Test
    void addSectionAsFirstStation() {
        // given
        // 지하철 역_생성
        StationResponse 신분당역 = StationAcceptanceTest.지하철역을_생성한다("신분당역");

        // when
        // 지하철_노선에_구간_등록_요청
        ExtractableResponse<Response> response = SectionAcceptanceFixture.구간_추가를_요청한다(lineResponse.getId(), 신분당역, 강남역, 4);

        // then
        // 지하철_노선에_지하철역_등록됨
        같은_응답인지_확인한다(response, HttpStatus.OK);
        // 지하철_노선_순서_확인
        지하철역이_같은순서인지_확인한다(response, 신분당역, 강남역, 역삼역);
    }

    @DisplayName("새로운 역을 하행 종점으로 등록한다.")
    @Test
    void addSectionAsLastStation() {
        // given
        // 지하철 역_생성
        StationResponse 잠실역 = StationAcceptanceTest.지하철역을_생성한다("잠실역");

        // when
        // 지하철_노선에_지하철역_등록_요청
        ExtractableResponse<Response> response = SectionAcceptanceFixture.구간_추가를_요청한다(lineResponse.getId(), 역삼역, 잠실역, 4);

        // then
        // 지하철_노선에_지하철역_등록됨
        같은_응답인지_확인한다(response, HttpStatus.OK);
        // 지하철_노선_순서_확인
        지하철역이_같은순서인지_확인한다(response, 강남역, 역삼역, 잠실역);
    }

    @DisplayName("역 사이에 새로운 역을 등록한다. (위쪽 부터)")
    @Test
    void addSectionAsNewStation() {
        // given
        // 지하철 역_생성
        StationResponse 강남역삼사이역 = StationAcceptanceTest.지하철역을_생성한다("강남역삼사이역");

        // when
        // 지하철_노선에_지하철역_등록_요청
        ExtractableResponse<Response> response = SectionAcceptanceFixture.구간_추가를_요청한다(lineResponse.getId(), 강남역, 강남역삼사이역, 4);

        // then
        // 지하철_노선에_지하철역_등록됨
        같은_응답인지_확인한다(response, HttpStatus.OK);
        // 지하철_노선_순서_확인
        지하철역이_같은순서인지_확인한다(response, 강남역, 강남역삼사이역, 역삼역);
    }

    @DisplayName("역 사이에 새로운 역을 등록한다. (아래쪽 부터)")
    @Test
    void addSectionAsNewStation2() {
        // given
        // 지하철 역_생성
        StationResponse 강남역삼사이역 = StationAcceptanceTest.지하철역을_생성한다("강남역삼사이역");

        // when
        // 지하철_노선에_지하철역_등록_요청
        ExtractableResponse<Response> response = SectionAcceptanceFixture.구간_추가를_요청한다(lineResponse.getId(), 강남역삼사이역, 역삼역, 4);

        // then
        // 지하철_노선에_지하철역_등록됨
        같은_응답인지_확인한다(response, HttpStatus.OK);
        // 지하철_노선_순서_확인
        지하철역이_같은순서인지_확인한다(response, 강남역, 강남역삼사이역, 역삼역);
    }

    @DisplayName("역 사이 요청 역의 길이가 기존 역 길이보다 긴 경우 등록할 수 없다.")
    @Test
    void addSectionWithLongerDistanceThanOrigin() {
        // given
        // 지하철 역_생성
        StationResponse 강남역삼사이역 = StationAcceptanceTest.지하철역을_생성한다("강남역삼사이역");


        // when
        // 지하철_노선에_지하철역_등록_요청
        ExtractableResponse<Response> response = SectionAcceptanceFixture.구간_추가를_요청한다(lineResponse.getId(), 강남역삼사이역, 역삼역, 10);

        // then
        // 지하철_노선에_지하철역_등록_실패
        같은_응답인지_확인한다(response, HttpStatus.BAD_REQUEST);
    }

    @DisplayName("기존에 두가지 역이 모두 등록 되어 있는 경우 등록할 수 없다.")
    @Test
    void addSectionWithAlreadyRegisteredStations() {
        // when
        // 지하철_노선에_지하철역_등록_요청
        ExtractableResponse<Response> response = SectionAcceptanceFixture.구간_추가를_요청한다(lineResponse.getId(), 강남역, 역삼역, 4);

        // then
        // 지하철_노선에_지하철역_등록_실패
        같은_응답인지_확인한다(response, HttpStatus.BAD_REQUEST);
    }

    @DisplayName("추가할 역이 모두 등록 되어 있지 않은 경우 등록할 수 없다.")
    @Test
    void addSectionWithNoRegisteredStations() {
        // given
        // 지하철 역_생성
        StationResponse 새로운역1 = StationAcceptanceTest.지하철역을_생성한다("새로운 역1");
        StationResponse 새로운역2 = StationAcceptanceTest.지하철역을_생성한다("새로운 역2");

        // when
        // 지하철_노선에_지하철역_등록_요청
        ExtractableResponse<Response> response = SectionAcceptanceFixture.구간_추가를_요청한다(lineResponse.getId(), 새로운역1, 새로운역2, 4);

        // then
        // 지하철_노선에_지하철역_등록_실패
        같은_응답인지_확인한다(response, HttpStatus.BAD_REQUEST);
    }

    @DisplayName("상행 종점을 삭제한다.")
    @Test
    void removeFirstStation() {
        // given
        // 지하철 역_생성
        StationResponse 신분당역 = StationAcceptanceTest.지하철역을_생성한다("신분당역");
        구간을_추가한다(lineResponse.getId(), 신분당역, 역삼역, 4);

        // when
        // 지하철_노선에_지하철역_삭제_요청


        // then
        // 지하철_노선에_지하철역_삭제됨
        // 지하철_노선_삭제_확인
    }

    @DisplayName("하행 종점을 삭제한다.")
    @Test
    void removeLastStation() {
        // given
        // 지하철 역_생성
        StationResponse 신분당역 = StationAcceptanceTest.지하철역을_생성한다("신분당역");
        구간을_추가한다(lineResponse.getId(), 신분당역, 역삼역, 4);

        // when
        // 지하철_노선에_지하철역_삭제_요청


        // then
        // 지하철_노선에_지하철역_삭제됨
        // 지하철_노선_삭제_확인
    }

    @DisplayName("중간 역을 삭제한다.")
    @Test
    void removeStationMiddle() {
        // given
        // 지하철 역_생성
        StationResponse 신분당역 = StationAcceptanceTest.지하철역을_생성한다("신분당역");
        구간을_추가한다(lineResponse.getId(), 신분당역, 역삼역, 4);

        // when
        // 지하철_노선에_지하철역_삭제_요청


        // then
        // 지하철_노선에_지하철역_삭제됨
        // 지하철_노선_삭제_확인
    }

    private LineResponse 구간을_추가한다(Long lineId, StationResponse upStation, StationResponse downStation, int distance) {
        return LineAcceptanceFixture.ofLineResponse(
                SectionAcceptanceFixture.구간_추가를_요청한다(lineId, upStation, downStation, distance)
        );
    }

    private void 같은_응답인지_확인한다(ExtractableResponse<Response> response, HttpStatus status) {
        assertThat(response.statusCode()).isEqualTo(status.value());
    }

    private void 지하철역이_같은순서인지_확인한다(ExtractableResponse<Response> response, StationResponse... stations) {
        assertThat(LineAcceptanceFixture.ofLineResponse(response).getStations())
                .containsExactly(stations);
    }
}
