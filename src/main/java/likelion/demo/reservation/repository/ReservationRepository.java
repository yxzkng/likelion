package likelion.demo.reservation.repository;

import likelion.demo.reservation.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @Query("SELECT r.store.id, r.startTime, SUM(r.headcount) " +
           "FROM Reservation r " +
           "WHERE r.date = :date " +
           "AND r.status <> likelion.demo.reservation.entity.ReservationStatus.CANCELED " +
           "GROUP BY r.store.id, r.startTime")
    List<Object[]> findReservedHeadcountByDate(@Param("date") LocalDate date);
}
