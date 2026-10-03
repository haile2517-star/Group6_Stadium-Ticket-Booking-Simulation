package org.stadium.repository;

import org.stadium.model.Match;
import org.stadium.model.enums.MatchStatus;
import java.util.List;

public class MatchRepository extends CsvRepository<Match>{
    public MatchRepository(String filePath){
        super(filePath);
    }


@Override 
public Match createEntity()
{
    return new Match();
}


@Override 
public String getHeader()
{
    return "matchId,organizerId,stadiumId,matchTitle,teamA,teamB,startTime,endTime,status";
}

public List<Match> findByStadiumId(String stadiumId)
{
    return findByCondition(m -> m.getStadiumId().equals(stadiumId));
}

public List<Match> findByStatus(MatchStatus status)
{
    return findByCondition(m -> m.getStatus() == status);
}

public List<Match> findUpcomingMatches(){
    return findByCondition(m -> m.getStatus() == MatchStatus.SCHEDULED);
}

}
