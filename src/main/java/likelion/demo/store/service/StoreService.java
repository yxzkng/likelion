package likelion.demo.store.service;

import likelion.demo.global.exception.NotFoundException;
import likelion.demo.reservation.repository.ReservationRepository;
import likelion.demo.store.dto.StoreListResponse;
import likelion.demo.store.dto.TimeslotResponse;
import likelion.demo.store.entity.Store;
import likelion.demo.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StoreService {

    private final StoreRepository storeRepository;
    private final ReservationRepository reservationRepository;

    public StoreListResponse getAvailableStores(LocalDate date, int headcount) {
        List<Store> stores = storeRepository.findAllWithClosedDays();

        DayOfWeek dayOfWeek = date.getDayOfWeek();

        // 휴무일인 술집 제외
        List<Store> openStores = stores.stream()
                .filter(store -> !store.getClosedDays().contains(dayOfWeek))
                .toList();

        // 해당 날짜의 예약 현황: storeId -> (startTime -> 예약 인원 합)
        Map<Long, Map<LocalTime, Long>> reservedMap = buildReservedMap(date);

        // 예약 가능한 술집 필터링
        List<StoreListResponse.StoreItem> storeItems = new ArrayList<>();

        for (Store store : openStores) {
            List<LocalTime> timeslots = generateTimeslots(store.getOpenTime(), store.getCloseTime());
            Map<LocalTime, Long> storeReserved = reservedMap.getOrDefault(store.getId(), Collections.emptyMap());

            int remainingSeatsMax = 0;
            boolean hasAvailableSlot = false;

            for (LocalTime slotStart : timeslots) {
                long reserved = storeReserved.getOrDefault(slotStart, 0L);
                int remaining = store.getSeatCapacity() - (int) reserved;
                remaining = Math.max(remaining, 0);

                if (remaining > remainingSeatsMax) {
                    remainingSeatsMax = remaining;
                }
                if (remaining >= headcount) {
                    hasAvailableSlot = true;
                }
            }

            if (hasAvailableSlot) {
                storeItems.add(StoreListResponse.StoreItem.builder()
                        .id(store.getId())
                        .name(store.getName())
                        .address(store.getAddress())
                        .openTime(store.getOpenTime())
                        .closeTime(store.getCloseTime())
                        .remainingSeatsMax(remainingSeatsMax)
                        .build());
            }
        }

        return StoreListResponse.builder()
                .date(date)
                .headcount(headcount)
                .stores(storeItems)
                .build();
    }

    public TimeslotResponse getTimeslots(Long storeId, LocalDate date, int headcount) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new NotFoundException("해당 술집을 찾을 수 없습니다."));

        List<LocalTime> timeslots = generateTimeslots(store.getOpenTime(), store.getCloseTime());
        Map<Long, Map<LocalTime, Long>> reservedMap = buildReservedMap(date);
        Map<LocalTime, Long> storeReserved = reservedMap.getOrDefault(storeId, Collections.emptyMap());

        List<TimeslotResponse.SlotItem> slotItems = new ArrayList<>();
        for (LocalTime slotStart : timeslots) {
            long reserved = storeReserved.getOrDefault(slotStart, 0L);
            int remainingSeats = Math.max(store.getSeatCapacity() - (int) reserved, 0);
            boolean available = remainingSeats >= headcount;

            slotItems.add(TimeslotResponse.SlotItem.builder()
                    .startTime(slotStart)
                    .remainingSeats(remainingSeats)
                    .available(available)
                    .build());
        }

        return TimeslotResponse.builder()
                .storeId(store.getId())
                .storeName(store.getName())
                .date(date)
                .slotCapacity(store.getSeatCapacity())
                .slots(slotItems)
                .build();
    }

    private Map<Long, Map<LocalTime, Long>> buildReservedMap(LocalDate date) {
        List<Object[]> results = reservationRepository.findReservedHeadcountByDate(date);
        Map<Long, Map<LocalTime, Long>> map = new HashMap<>();

        for (Object[] row : results) {
            Long storeId = (Long) row[0];
            LocalTime startTime = (LocalTime) row[1];
            Long totalHeadcount = (Long) row[2];

            map.computeIfAbsent(storeId, k -> new HashMap<>())
                    .put(startTime, totalHeadcount);
        }

        return map;
    }

    private List<LocalTime> generateTimeslots(LocalTime openTime, LocalTime closeTime) {
        List<LocalTime> slots = new ArrayList<>();
        LocalTime current = openTime;

        for (int i = 0; i < 24; i++) {
            if (!current.isBefore(closeTime)) {
                break;
            }
            slots.add(current);
            current = current.plusHours(1);
            // 자정(00:00)을 넘어가면 종료
            if (current.equals(LocalTime.MIDNIGHT) || current.isBefore(openTime)) {
                break;
            }
        }

        return slots;
    }
}
