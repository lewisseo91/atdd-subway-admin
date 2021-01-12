package nextstep.subway.section.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

import lombok.Getter;
import nextstep.subway.station.domain.Station;

@Getter
@Embeddable
public class Sections {
	private final static int SECTIONS_MINIMUM_SIZE = 1;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "line_id")
	private List<Section> sections = new ArrayList<>();

	public void addInitSection(Section section) {
		sections.add(section);
	}

	public void addNewSection(Section section) {
		validateSection(section);
		sections.stream()
			.filter(s -> s.isSameUpStationId(section.getUpStation()))
			.findFirst()
			.ifPresent(s -> s.addUpStation(section.getDownStation(), section.getDistance()));
		sections.stream()
			.filter(s -> s.isSameDownStationId(section.getDownStation()))
			.findFirst()
			.ifPresent(s -> s.addDownStation(section.getUpStation(), section.getDistance()));
		sections.add(section);
	}

	private void validateSection(Section section) {
		boolean isBothExist = sections.stream()
			.anyMatch(s -> s.isSameUpStationId(section.getUpStation())
				&& s.isSameDownStationId(section.getDownStation()));
		if (isBothExist) {
			throw new IllegalArgumentException("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없습니다.");
		}
		sections.stream()
			.filter(s -> s.isSameUpStationId(section.getUpStation())
				|| s.isSameDownStationId(section.getDownStation())
				|| s.isSameUpStationId(section.getDownStation())
				|| s.isSameDownStationId(section.getUpStation()))
			.findFirst().orElseThrow(() -> new IllegalArgumentException("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없습니다."));
	}

	public void removeSection(Station station) {
		validateRemoveSection();
		boolean isUpExist = sections.stream()
			.anyMatch(s -> s.isSameUpStationId(station));
		boolean isDownExist = sections.stream()
			.anyMatch(s -> s.isSameDownStationId(station));
		if (isUpExist && !isDownExist) {
			sections.stream()
				.filter(s -> s.isSameUpStationId(station) && !s.isSameDownStationId(station))
				.findFirst()
				.ifPresent(s -> sections.remove(s));
		}
		if (!isUpExist && isDownExist) {
			sections.stream()
				.filter(s -> s.isSameDownStationId(station) && !s.isSameUpStationId(station))
				.findFirst()
				.ifPresent(s -> sections.remove(s));
		}
		if (isUpExist && isDownExist) {
			Section downSection = sections.stream()
				.filter(s -> s.isSameDownStationId(station))
				.findFirst()
				.get();
			Section upSection = sections.stream()
				.filter(s -> s.isSameUpStationId(station))
				.findFirst()
				.get();
			Station downStation = upSection.getDownStation();
			Distance distance = upSection.getDistance();
			sections.remove(upSection);
			downSection.removeDownStation(downStation, distance);
		}
		if (!isUpExist && !isDownExist) {
			throw new IllegalArgumentException("해당 노선에 등록된 역이 아닙니다.");
		}
	}

	private void validateRemoveSection() {
		if (sections.size() <= SECTIONS_MINIMUM_SIZE) {
			throw new IllegalArgumentException("구간이 하나인 경우 삭제가 불가합니다.");
		}
	}
}