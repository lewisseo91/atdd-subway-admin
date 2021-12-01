package nextstep.subway.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.StationAcceptanceFixture;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;


@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {
    @DisplayName("지하철 노선을 구역과 함께 생성할 수 있다.")
    @Test
    void createLineWithSection() {
        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response =
                requestCreateLineWithStation("역삼역", "강남역", 9, "2호선", "green");

        // then
        // 지하철_노선_생성됨
        checkResponseStatus(response, HttpStatus.CREATED);
    }

    @DisplayName("지하철 노선을 구역 없이 생성할 수 없다.")
    @Test
    void createLineWithoutSectionException() {
        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response = LineAcceptanceFixture.requestCreateLine("2호선", "green");

        // then
        // 지하철_노선_생성됨
        checkResponseStatus(response, HttpStatus.BAD_REQUEST);
    }


    @DisplayName("지하철 노선 중복으로 생성할 수 없다.")
    @Test
    void createLine2() {
        // given
        // 지하철_노선_등록되어_있음
        requestCreateLineWithStation("역삼역", "강남역", 9, "2호선", "green");

        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response =
                requestCreateLineWithStation("신사역", "삼성역", 9, "2호선", "green");

        // then
        // 지하철_노선_생성_실패됨
        checkResponseStatus(response, HttpStatus.BAD_REQUEST);
    }

    @DisplayName("지하철 노선 목록을 구간과 함께 조회한다.")
    @Test
    void getLinesWithSection() {
        // given
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> createdResponse1 =
                requestCreateLineWithStation("역삼역", "강남역", 9, "2호선", "green");
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> createdResponse2 =
                requestCreateLineWithStation("신사역", "삼성역", 6, "3호선", "orange");

        // when
        // 지하철_노선_목록_조회_요청
        ExtractableResponse<Response> response = LineAcceptanceFixture.requestGetLines();

        // then
        // 지하철_노선_목록_응답됨
        checkResponseStatus(response, HttpStatus.OK);
        // 지하철_노선_목록_포함됨
        checkContainsLine(LineAcceptanceFixture.ofLineResponses(createdResponse1, createdResponse2), response);

    }

    @DisplayName("지하철 노선을 구간과 함께 조회한다.")
    @Test
    void getLineWithSection() {
        // given
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> createdResponse =
                requestCreateLineWithStation("역삼역", "강남역", 9, "2호선", "green");
        LineResponse createdLineResponse = LineAcceptanceFixture.ofLineResponse(createdResponse);

        // when
        // 지하철_노선_조회_요청
        ExtractableResponse<Response> response = LineAcceptanceFixture.requestGetLineById(createdLineResponse.getId());

        // then
        // 지하철_노선_응답됨
        checkResponseStatus(response, HttpStatus.OK);
        // 지하철_노선_동일_확인됨
        checkSameLine(createdResponse, response);
    }


    @DisplayName("지하철 노선을 구간과 함께 제거할 수 있다.")
    @Test
    void deleteLineWithSection() {
        // given
        // 지하철_노선_등록되어_있음
        LineResponse createdLineResponse = LineAcceptanceFixture.ofLineResponse(
                requestCreateLineWithStation("역삼역", "강남역", 9, "2호선", "green")
        );

        // when
        // 지하철_노선_제거_요청
        ExtractableResponse<Response> response = LineAcceptanceFixture.requestDeleteLine(createdLineResponse.getId());

        // then
        // 지하철_노선_삭제됨
        checkResponseStatus(response, HttpStatus.NO_CONTENT);
        // 지하철_노선_삭제_확인됨
        ExtractableResponse<Response> getResponse = LineAcceptanceFixture.requestGetLineById(createdLineResponse.getId());
        checkResponseStatus(getResponse, HttpStatus.BAD_REQUEST);
    }

    public static ExtractableResponse<Response> requestCreateLineWithStation(String upStationName, String downStationName, int distance, String lineName, String colorName) {
        StationResponse upStation = StationAcceptanceFixture.ofStationResponse(StationAcceptanceFixture.requestCreateStations(upStationName));
        StationResponse downStation = StationAcceptanceFixture.ofStationResponse(StationAcceptanceFixture.requestCreateStations(downStationName));
        Map<String, String> params = LineAcceptanceFixture.createParams(lineName, colorName, upStation.getId(), downStation.getId(), distance);
        return LineAcceptanceFixture.requestCreateLine(params);
    }

    public static ExtractableResponse<Response> requestCreateLineWithStation(StationResponse upStation, StationResponse downStation, int distance, String lineName, String colorName) {
        Map<String, String> params = LineAcceptanceFixture.createParams(lineName, colorName, upStation.getId(), downStation.getId(), distance);
        return LineAcceptanceFixture.requestCreateLine(params);
    }

    private void checkSameLine(ExtractableResponse<Response> createdResponse, ExtractableResponse<Response> originResponse) {
        assertThat(LineAcceptanceFixture.ofLineResponse(createdResponse)).isEqualTo(LineAcceptanceFixture.ofLineResponse(originResponse));
    }

    private void checkContainsLine(List<LineResponse> expectedLines, ExtractableResponse<Response> response) {
        List<LineResponse> lines = LineAcceptanceFixture.ofLineResponses(response);
        assertThat(lines).containsAll(expectedLines);
    }

    private void checkResponseStatus(ExtractableResponse<Response> response, HttpStatus status) {
        assertThat(response.statusCode()).isEqualTo(status.value());
    }
}
