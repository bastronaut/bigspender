package com.bastronaut.bigspender.services;

import com.bastronaut.bigspender.dto.shared.LabelDTO;
import com.bastronaut.bigspender.dto.shared.LinkDTO;
import com.bastronaut.bigspender.dto.shared.LinksDTO;
import com.bastronaut.bigspender.models.Label;
import com.bastronaut.bigspender.models.Transaction;
import com.bastronaut.bigspender.models.User;
import com.bastronaut.bigspender.repositories.LabelRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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

    public List<Label> deleteLabels(final List<Long> ids, final User user) {

        final List<Label> labels = getLabelsById(ids, user);

        // We ensure the relationship between Label and Transaction in the join-table is removed
        for (final Label label: labels) {
            final Set<Transaction> transactions = label.getTransactions();
            for (Transaction transaction: transactions) {
                // have to manually delete here to avoid concurrency problems with emanaged List<Label> labels iterator
                transaction.getLabels().remove(label);
            }
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


    /**
     * Given a LinksDTO, will 'link' (add) the labels corresponding to the label id to the
     * transactionids. Will return a new linkLabelsToTransactionsDTO that only contains the transaction ids and
     * label ids that belong to the user
     * @param linksToAdd
     * @param user
     * @return
     */
    public LinksDTO linkLabelsToTransactions(final LinksDTO linksToAdd,
                                             final User user) {

        final List<LinkDTO> linksToUpdate = linksToAdd.getLinks();
        final List<LinkDTO> updated = linksToUpdate.stream()
                .map(l -> addLabelsToTransaction(l.getTransactionId(), l.getLabelIds(), user))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        return new LinksDTO(updated);
    }

    /**
     * Returns null if the transaction doesn't exist for the user
     * @param transactionId corresponding to the transaction for which to link labels
     * @param labelIds corresponding to the labels to link to the transaction
     * @param user
     */
    @Nullable
    private LinkDTO addLabelsToTransaction(final long transactionId, final Set<Long> labelIds, final User user) {

        final List<Long> labelIdsToRetrieve = new ArrayList<>(labelIds);
        final List<Label> labelsToAdd = getLabelsById(labelIdsToRetrieve, user);
        if (!labelsToAdd.isEmpty()) {

            final Optional<Transaction> maybeTransaction = transactionService.getTransactionForUser(transactionId, user);
            if (maybeTransaction.isPresent()) {
                final Transaction transaction = maybeTransaction.get();
                final Set<Long> addedLabelsIds = new HashSet<>();
                labelsToAdd.forEach(l -> {
                    transaction.addLabel(l);
                    addedLabelsIds.add(l.getId());
                });
                this.saveLabels(labelsToAdd);
                transactionService.saveTransaction(transaction);
                return new LinkDTO(transaction.getId(), addedLabelsIds);
            }
        }
        return null;
    }

    /**
     * Given a LinksDTO, will 'unlink' (remove) the labels corresponding to the label id to the
     * transactionids. Will return a new LinkDTO that only contains the transaction ids and
     * label ids that belong to the user
     * @param linksToRemove
     * @param user
     * @return
     */
    public LinksDTO unlinkLabelsFromTransactions(final LinksDTO linksToRemove, final User user) {
        final List<LinkDTO> linksToUpdate = linksToRemove.getLinks();
        final List<LinkDTO> updated = linksToUpdate.stream()
                .map(l -> removeLabelsFromTransaction(l.getTransactionId(), l.getLabelIds(), user))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        return new LinksDTO(updated);
    }

    /**
     * Returns null if the transaction doesn't exist for the user
     * @param transactionId corresponding to the transaction for which to unlink labels
     * @param labelIds corresponding to the labels to unlink from the transaction
     * @param user
     * @return
     */
    @Nullable
    private LinkDTO removeLabelsFromTransaction(final long transactionId, final Set<Long> labelIds, final User user) {
        final List<Long> labelIdsToRetrieve = new ArrayList<>(labelIds);
        final List<Label> labelsToRemove = getLabelsById(labelIdsToRetrieve, user);
        if (!labelsToRemove.isEmpty()) {
            final Optional<Transaction> maybeTransaction = transactionService.getTransactionForUser(transactionId, user);
            if (maybeTransaction.isPresent()) {
                final Transaction transaction = maybeTransaction.get();
                final Set<Long> removedLabelsIds = new HashSet<>();
                labelsToRemove.forEach(l -> {

                    transaction.getLabels().remove(l);
//
//                    transaction.removeLabel(l);

                    removedLabelsIds.add(l.getId());
                });
                this.saveLabels(labelsToRemove);
                transactionService.saveTransaction(transaction);
                return new LinkDTO(transaction.getId(), removedLabelsIds);
            }
        }
        return null;
    }


}
