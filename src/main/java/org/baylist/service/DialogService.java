package org.baylist.service;

import lombok.RequiredArgsConstructor;
import org.baylist.db.entity.Dialog;
import org.baylist.db.repo.DialogRepository;
import org.baylist.dto.telegram.State;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DialogService {

	private final DialogRepository dialogs;

	public Dialog getDialog(long userId) {
		return dialogs.findByUserId(userId);
	}

	@Transactional(readOnly = true)
	public State getState(long userId) {
		return dialogs.findByUserId(userId).state();
	}

	@Transactional
	public void setState(long userId, State state) {
		int updated = dialogs.updateState(userId, state);
		if (updated == 0) {
			dialogs.save(new Dialog(null, userId, userId, state));
		}
	}

	@Transactional
	public void saveDialog(Dialog dialog) {
		dialogs.save(dialog);
	}

}
