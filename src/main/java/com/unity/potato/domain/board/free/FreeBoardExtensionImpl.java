package com.unity.potato.domain.board.free;

import com.querydsl.core.QueryResults;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.hibernate.cache.spi.QueryResultsCache;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class FreeBoardExtensionImpl extends QuerydslRepositorySupport implements FreeBoardExtension {
    private final JPAQueryFactory jpaQueryFactory;
    private final QFreeBoard qFreeBoard = QFreeBoard.freeBoard;
    public FreeBoardExtensionImpl(JPAQueryFactory jpaQueryFactory) {
        super(FreeBoard.class);
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    public Page<FreeBoard> searchByTitle(String keyword, Pageable pageable) {
        QFreeBoard freeBoard = QFreeBoard.freeBoard;
        JPQLQuery<FreeBoard> query = from(freeBoard).where(freeBoard.deleteYn.ne('Y')
                .and(freeBoard.title.containsIgnoreCase(keyword)));

        JPQLQuery<FreeBoard> pageableQuery = getQuerydsl().applyPagination(pageable, query);
        QueryResults<FreeBoard> fetchResults = pageableQuery.fetchResults();

        return new PageImpl<>(fetchResults.getResults(), pageable, fetchResults.getTotal());
    }

    @Override
    public Page<FreeBoard> searchByContent(String keyword, Pageable pageable) {
        QFreeBoard freeBoard = QFreeBoard.freeBoard;
        JPQLQuery<FreeBoard> query = from(freeBoard).where(freeBoard.deleteYn.ne('Y')
                .and(freeBoard.content.containsIgnoreCase(keyword)));

        JPQLQuery<FreeBoard> pageableQuery = getQuerydsl().applyPagination(pageable, query);
        QueryResults<FreeBoard> fetchResults = pageableQuery.fetchResults();

        return new PageImpl<>(fetchResults.getResults(), pageable, fetchResults.getTotal());
    }

    @Override
    public Page<FreeBoard> searchByTitleAndContent(String keyword, Pageable pageable) {
        QFreeBoard freeBoard = QFreeBoard.freeBoard;
        JPQLQuery<FreeBoard> query = from(freeBoard).where(freeBoard.deleteYn.ne('Y')
                .and(freeBoard.title.containsIgnoreCase(keyword)
                        .or(freeBoard.content.containsIgnoreCase(keyword))));

        JPQLQuery<FreeBoard> pageableQuery = getQuerydsl().applyPagination(pageable, query);
        QueryResults<FreeBoard> fetchResults = pageableQuery.fetchResults();

        return new PageImpl<>(fetchResults.getResults(), pageable, fetchResults.getTotal());
    }

    @Override
    public long countSearch(String keyword) {
        QFreeBoard freeBoard = QFreeBoard.freeBoard;
        JPQLQuery<FreeBoard> query = from(freeBoard)
                .where(freeBoard.deleteYn.ne('Y')
                .and(freeBoard.title.containsIgnoreCase(keyword)
                    .or(freeBoard.content.containsIgnoreCase(keyword))));

        return query.fetchCount();
    }

    @Override
    public List<FreeBoard> searchBoardLimit3(String keyword) {
        QFreeBoard freeBoard = QFreeBoard.freeBoard;
        JPQLQuery<FreeBoard> query = from(freeBoard).where(freeBoard.deleteYn.ne('Y')
                        .and(freeBoard.title.containsIgnoreCase(keyword)
                                .or(freeBoard.content.containsIgnoreCase(keyword))))
                .orderBy(freeBoard.regDt.desc()) // 최신순 정렬
                .limit(3); // 최대 3개 결과만 가져옴

        return query.fetch();
    }
}
