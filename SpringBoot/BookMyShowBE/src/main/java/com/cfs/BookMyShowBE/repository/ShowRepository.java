package com.cfs.BookMyShowBE.repository;

import com.cfs.BookMyShowBE.entity.Show;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface ShowRepository extends JpaRepository<Show,Long> {

    boolean existsByMovieIdAndTheatreIdAndStartsAt(Long movieId, Long theatreId, LocalDateTime startsAt);

    @Query("select s from Show s join fetch s.movie m join fetch s.theatre t "+
          "where s.active = true and m.active = true and t.city = :city "+
         "and s.startsAt >= :from and s.startsAt < :to order by s.startsAt")
    List<Show> findActiveShows(String city,LocalDateTime from,LocalDateTime to);
}
