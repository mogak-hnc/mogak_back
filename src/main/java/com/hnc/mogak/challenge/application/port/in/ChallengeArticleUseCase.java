package com.hnc.mogak.challenge.application.port.in;

import com.hnc.mogak.challenge.application.port.in.command.CreateArticleCommand;

public interface ChallengeArticleUseCase {

    Long create(CreateArticleCommand command);

}
