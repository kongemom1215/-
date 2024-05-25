package com.unity.potato.domain.board.worldcup;

import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.unity.potato.domain.board.share.ShareBoard;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

public class WorldcupDetailExtensionImpl extends QuerydslRepositorySupport implements WorldcupDetailExtension{

    private final JPAQueryFactory jpaQueryFactory;
    public WorldcupDetailExtensionImpl(JPAQueryFactory jpaQueryFactory) {
        super(WorldcupDetail.class);
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    public Integer getParticipateCnt(long worldcupId) {
        QWorldcupDetail worldcupDetail = QWorldcupDetail.worldcupDetail;
        JPQLQuery<Integer> query = from(worldcupDetail)
                .select(worldcupDetail.winCnt.sum())
                .from(worldcupDetail)
                .where(worldcupDetail.worldcupId.eq(worldcupId));

        return query.fetchOne();
    }
}
