package com.quinto.ga.songsapi.repositories;

import com.quinto.ga.songsapi.models.Song;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface SongRepository extends CrudRepository<Song, Long> {
    List<Song> findByTitleContaining(String title);
    List<Song> findByLengthBetween(int min, int max);
    List<Song> findByContentContaining(String content);
}
