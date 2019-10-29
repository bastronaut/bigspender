import * as types from '../constants/ActionTypes'

/**
 * Action creators, functions that create actions
 */

export function addTodo(text) {
    return { type: ADD_TODO, text }
}