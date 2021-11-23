package com.aldren.messaging.repository;

import com.aldren.messaging.document.Messages;

import java.util.Date;
import java.util.List;

public interface CustomMessageRepository {

    int messageCountByDateDuration(Date startDate, Date endDate);

}
