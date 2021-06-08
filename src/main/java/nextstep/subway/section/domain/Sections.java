package nextstep.subway.section.domain;

import nextstep.subway.station.domain.Station;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = {CascadeType.REMOVE, CascadeType.PERSIST})
    private List<Section> sections = new ArrayList<>();

    public Sections() { }

    public Stream<Section> stream() {
        return sections.stream();
    }

    public void add(Section section) {
        if (!this.contains(section)) {
            sections.add(section);
        }
    }

    public void add(int index, Section section) {
        if (!this.contains(section)) {
            sections.add(index, section);
        }
    }

    public boolean contains(Section section) {
        return sections.contains(section);
    }

    public List<Station> getOrderedStations() {
        return makeOrderedSectionsFrom(findTopSection()).stream()
                .flatMap(section -> Stream.of(section.getUpStation(), section.getDownStation()))
                .distinct()
                .collect(Collectors.toList());
    }

     public List<Section> orderFromTopToBottom() {
        sections = makeOrderedSectionsFrom(findTopSection());
        return sections;
    }

    public Section findTopSection() {
        return sections.stream()
                .filter(section -> isTop(section))
                .findFirst()
                .orElseThrow(NullPointerException::new);
    }

    private boolean isTop(Section section) {
        return !sections.stream()
                .filter(it -> it.getDownStation().getName()
                        .equals(section.getUpStation().getName()))
                .findAny()
                .isPresent();
    }

    private List<Section> makeOrderedSectionsFrom(Section topSection) {
        List<Section> orderedSections = new ArrayList<>();
        orderedSections.add(topSection);

        Map<String, Section> upNameMap = new HashMap<>();
        for (Section it : this.sections) {
            upNameMap.put(it.getUpStation().getName(), it);
        }

        while (topSection != null) {
            topSection = upNameMap.get(topSection.getDownStation().getName());

            addNextSection(orderedSections, topSection);
        }

        return orderedSections;
    }

    private void addNextSection(List<Section> orderedSections, Section section) {
        if (section == null) {
            return;
        }
        if (orderedSections.contains(section)) {
            return;
        }
        orderedSections.add(section);
    }

    public boolean isEmpty() {
        return sections.isEmpty();
    }

}
