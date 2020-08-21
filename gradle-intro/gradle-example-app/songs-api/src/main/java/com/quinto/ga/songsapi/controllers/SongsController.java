package com.quinto.ga.songsapi.controllers;

import com.quinto.ga.songsapi.models.Song;
import com.quinto.ga.songsapi.repositories.SongRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class SongsController {
    @Autowired
    private SongRepository songRepo;

    @GetMapping("/all")
    public Iterable<Song> getAll() {
        return songRepo.findAll();
    }

    @GetMapping("/view/{id}")
    public Song searchById(@PathVariable long id) {
        return songRepo.findOne(id);
    }

    @GetMapping("/searchTitle/{title}")
    public Iterable<Song> searchByTitle(@PathVariable String title) {
        return songRepo.findByTitleContaining(title);
    }

    @GetMapping("/searchLength")
    public List<Song> searchByLength(@RequestParam int min, @RequestParam int max) {
        return songRepo.findByLengthBetween(min, max);
    }

    @GetMapping("/searchContent/{content}")
    public List<Song> searchByContent(@PathVariable String content) {
        return songRepo.findByContentContaining(content);
    }

    @DeleteMapping("/delete/{id}")
    public HttpStatus deleteSong(@PathVariable long id) {
        songRepo.delete(id);
        return HttpStatus.OK;
    }

    @PostMapping("/create")
    public HttpStatus createSong(@RequestBody Song Song) {
        songRepo.save(Song);
        return HttpStatus.OK;
    }

    @PatchMapping("/update/{id}")
    public HttpStatus updateSong(@PathVariable long id, @RequestBody Song SongRequest) {
        Song Song = songRepo.findOne(id);
        Song.setTitle(SongRequest.getTitle());
        Song.setLength(SongRequest.getLength());
        Song.setContent(SongRequest.getContent());
        songRepo.save(Song);
        return HttpStatus.OK;
    }
}
