package ru.practicum.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.CommentByUserDto;
import ru.practicum.dto.CommentDto;
import ru.practicum.dto.CommentFromRequestDto;
import ru.practicum.dto.CommentUpdateDto;
import ru.practicum.dto.enums.State;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.CommentMapper;
import ru.practicum.model.Comment;
import ru.practicum.model.Event;
import ru.practicum.model.User;
import ru.practicum.repository.CommentRepository;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.UserRepository;
import ru.practicum.service.CommentService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final CommentRepository commentRepository;

    @Override
    @Transactional
    public CommentDto create(CommentFromRequestDto comment, Long eventId, Long userId) {
        Event event = getPublishedEventOrException(eventId);
        User user = getUserOrException(userId);
        Comment savedComment = commentRepository.save(CommentMapper.toCommentFromRequest(comment, user, event));
        return CommentMapper.toCommentDto(savedComment);
    }

    @Override
    public List<CommentDto> getAllByEvent(Long eventId, PageRequest page) {
        getPublishedEventOrException(eventId);
        return commentRepository.findAll(page)
                .stream().map(CommentMapper::toCommentDto).collect(Collectors.toList());
    }

    @Override
    public List<CommentByUserDto> getAllByUser(Long userId, PageRequest page) {
        getUserOrException(userId);
        return commentRepository.findAllByAuthorId(userId, page)
                .stream().map(CommentMapper::toCommentByUserDto).collect(Collectors.toList());
    }

    @Override
    public CommentDto getById(Long commentId, Long userId) {
        getUserOrException(userId);
        Comment comment = getCommentOrException(commentId);
        return CommentMapper.toCommentDto(comment);
    }

    @Override
    @Transactional
    public CommentUpdateDto update(CommentFromRequestDto comment, Long userId, Long commentId) {
        getUserOrException(userId);
        Comment old = getCommentOrException(commentId);
        Comment update = CommentMapper.toCommentFromRequest(comment, old);
        return CommentMapper.toCommentUpdateDto(commentRepository.save(update), old.getText());
    }

    @Override
    @Transactional
    public void delete(Long commentId, Long userId) {
        getUserOrException(userId);
        getCommentOrException(commentId);
        commentRepository.deleteById(commentId);

    }

    private Event getPublishedEventOrException(Long eventId) {
        return eventRepository.findByIdAndState(eventId, State.PUBLISHED).orElseThrow(
                () -> new NotFoundException("Событие не найдено."));
    }

    private User getUserOrException(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("Пользователь не найден."));
    }

    private Comment getCommentOrException(Long commentId) {
        return commentRepository.findById(commentId).orElseThrow(
                () -> new NotFoundException("Комментарий не найден."));
    }

}
