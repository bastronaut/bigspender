package com.bastronaut.bigspender.services;

import com.bastronaut.bigspender.dto.in.LabelUpdateDTO;
import com.bastronaut.bigspender.dto.shared.LabelDTO;
import com.bastronaut.bigspender.dto.shared.LinkLabelsToTransactionDTO;
import com.bastronaut.bigspender.dto.shared.LinkLabelsToTransactionsDTO;
import com.bastronaut.bigspender.models.Label;
import com.bastronaut.bigspender.models.Transaction;
import com.bastronaut.bigspender.models.User;
import com.bastronaut.bigspender.repositories.LabelRepository;
import com.bastronaut.bigspender.repositories.TransactionRepository;
import jdk.nashorn.internal.ir.Labels;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.bastronaut.bigspender.utils.ApplicationConstants.DEFAULT_LABELCOLOR;
import static com.bastronaut.bigspender.utils.ApplicationConstants.DEFAULT_LABELNAME;

@Service
public class LabelService {

    private final LabelRepository labelRepository;

    private final TransactionService transactionService;


    /**
     * We need a TransactionService here in order to delete the links that exist between the @ManyToMany
     * relationship of labels and links. Only the 'owner' of the relationship (the entity with JoinColumn, not the
     * entity with @mappedBy) has automatically deleted links, the other way around not so.
     * @param labelRepository
     * @param transactionService
     */
    @Autowired
    public LabelService(final LabelRepository labelRepository, final TransactionService transactionService) {
        this.labelRepository= labelRepository;
        this.transactionService = transactionService;
    }


    public List<Label> getLabelsById(final List<Long> labelIds, final User user) {
        return labelRepository.findByIdInAndUser(labelIds, user);
    }

    public Set<Label> getLabelsByTransactionId(final long transactionId, final User user) {
        return labelRepository.findByTransactions_idAndUser(transactionId, user);
    }

    public List<Label> getLabels(final User user) {
        return labelRepository.findAllByUser(user);
    }

    public List<Label> saveLabels(final List<Label> labels) {
        return labelRepository.saveAll(labels);
    }

    public Label saveLabel(final Label label) {
        return labelRepository.save(label);
    }


    public List<Label> deleteLabels(final List<Long> ids, final User user) {

        final List<Label> labels = getLabelsById(ids, user);

        // We ensure the relationship between Label and Transaction in the join-table is removed
        for (Label label: labels) {
            Set<Transaction> transactions = label.getTransactions();
            for (Transaction transaction: transactions) {
                transaction.getLabels().remove(label);
            }// if size >0 save
            transactionService.saveTransactions(transactions);
        }
        return labelRepository.deleteByIdInAndUser(ids, user);
    }


    /**
     * Accepts a list of labelDTOs to update. In PUT fashion this method will create the Label if it does not yet
     * exist, using some default values if incomplete data is provided.
     * @param labelDTOs
     * @param user
     * @return
     */
    public List<Label> updateLabels(List<LabelDTO> labelDTOs, User user) {

        // Group up labels to modify, to avoid independent db calls
        final List<Long> labelIdsToModify = new ArrayList<>();
        for (final LabelDTO labelDTO: labelDTOs) {
            labelIdsToModify.add(labelDTO.getId());
        }

        final List<Label> labelsToUpdate = labelRepository.findByIdInAndUser(labelIdsToModify, user);

        // Set Labels to update in kv map to be able to set updates easier
        final HashMap<Long, Label> labelsById = new HashMap<>();
        labelsToUpdate.forEach(l -> labelsById.put(l.getId(), l));

        for (final LabelDTO labelDTO: labelDTOs) {
            final Label updateLabel = labelsById.get(labelDTO.getId());

            if (updateLabel == null) {
                // label to update wasn't present in DB, create it
                final Label newLabel = createNewLabelFromUpdateDTO(labelDTO, user);
                labelsToUpdate.add(newLabel);
                continue;
            }

            if (StringUtils.isNotEmpty(labelDTO.getColor())) {
                updateLabel.setColor(labelDTO.getColor());
            }

            if (StringUtils.isNotEmpty(labelDTO.getName())) {
                updateLabel.setName(labelDTO.getName());
            }
        }

        return labelRepository.saveAll(labelsToUpdate);
    }

    private Label createNewLabelFromUpdateDTO(final LabelDTO labelDTO, final User user) {
        final String name = StringUtils
                .isNotBlank(labelDTO.getName()) ? labelDTO.getName() : DEFAULT_LABELNAME;
        final String color = StringUtils
                .isNotBlank(labelDTO.getColor()) ? labelDTO.getColor() : DEFAULT_LABELCOLOR;

        return new Label(name, user, color);
    }


    public void linkLabelsToTransactions(final LinkLabelsToTransactionsDTO linkLabelsToTransactionsDTO, final User user) {

        final List<LinkLabelsToTransactionDTO> linksToUpdate = linkLabelsToTransactionsDTO.getLinks();
        linksToUpdate.stream();
    }

    /**
     *
     * @param transactionId
     * @param labelIds
     * @param user
     */
    private void addLabelsToTransaction(final long transactionId, final Set<Long> labelIds, final User user) {
        final List<Long> labelIdsToRetrieve = new ArrayList<>(labelIds);
        final List<Label> labelsToAdd = getLabelsById(labelIdsToRetrieve, user);
        if (labelsToAdd.isEmpty()) {
            return; // todo
        }
        // add both ways, make this more readable
        transactionService.getTransactionForUser(transactionId, user).ifPresent(t -> {
            final Set<Label> currentLabels = t.getLabels();
            labelsToAdd.forEach(l -> {
                final boolean addedLabel = currentLabels.add(l);
                final boolean addedTransaction = l.getTransactions().add(t);
            });
            transactionService.saveTransaction(t, user); //
        });



        return; // todo

    }


}
