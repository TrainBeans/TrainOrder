package org.trainbeans.trainorder.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Domain model representing a completed railroad Form 19 Train Order.
 * Each field maps to one underlined blank on the paper form.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrainOrder {

    /** Auto-generated primary key. */
    private Long id;

    // ── Header section ────────────────────────────────────────────────────────

    /** "TRAIN ORDER No." blank. */
    private String orderNumber;

    /** Form-type blank printed after "FORM 19" in the header (e.g. "19", "31"). */
    private String formType;

    /** "From" blank – issuing station. */
    private String fromLocation;

    /** "Date" blank (e.g. "March 27, 2026"). */
    private String orderDate;

    /** "To" blank – addressed train/engine/crew. */
    private String toLine;

    /** "Opr.;" blank – issuing operator initials. */
    private String operatorInitials;

    /** Time blank immediately after "Opr.;" (e.g. "9:45 A"). Pre-printed "M." follows on the form. */
    private String timeIssued;

    /** "At" blank – location where order is delivered. */
    private String atLocation;

    // ── Instructions body (large centre blank) ────────────────────────────────

    /** Free-text instructions; may contain several paragraphs. */
    private String instructions;

    // ── C.T.D. section (below instructions) ──────────────────────────────────

    /** C.T.D. blank – dispatcher's name or initials who authorised the order. */
    private String dispatcherInitials;

    // ── Completion section ────────────────────────────────────────────────────

    /** "complete time" blank (e.g. "10:02 A"). Pre-printed "m." follows. */
    private String completeTime;

    /** Operator initials blank that follows the complete-time line. */
    private String completeOperator;

    // ── Recopy section ────────────────────────────────────────────────────────

    /** "recopied by" blank. */
    private String recopiedBy;

    /** "opr.:" blank in the recopy line. */
    private String recopyOperator;

    /** "date" blank in the recopy line. */
    private String recopyDate;

    // ── Lifecycle ─────────────────────────────────────────────────────────────

    /** When true the order is locked and cannot be edited. */
    private boolean completed;

    // ── Metadata ──────────────────────────────────────────────────────────────

    /** Timestamp set automatically when the record is first persisted. */
    private LocalDateTime createdAt;
}

