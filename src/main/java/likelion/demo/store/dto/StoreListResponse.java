package likelion.demo.store.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class StoreListResponse {

    private LocalDate date;
    private int headcount;
    private List<StoreItem> stores;

    @Getter
    @AllArgsConstructor
    @Builder
    public static class StoreItem {
        private Long id;
        private String name;
        private String address;

        @JsonFormat(pattern = "HH:mm")
        private LocalTime openTime;

        @JsonFormat(pattern = "HH:mm")
        private LocalTime closeTime;

        private int remainingSeatsMax;
    }
}
